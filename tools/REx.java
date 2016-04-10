import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

/**
 * <h3>REx command line client</h3>
 * <p>
 * Execute REx parser generator remotely by posting a multipart HTTP
 * request to the <a href="http://www.bottlecaps.de/rex/">REx server</a>.
 * <p>
 * This program accepts command lines as can be configured on the REx
 * server webpage, augmented with the grammar file name, and it will send
 * an HTTP request for remote parser generation. The resulting files are
 * stored in the current directory. Any messages will be shown on standard
 * output.
 *
 * @author Gunther Rademacher <a href="mailto:grd@gmx.net">grd@gmx.net</a>
 * @version 5.38.1224
 */
public class REx
{
  private static final String REX_URL = "http://www.bottlecaps.de/rex/";
  private static final String USER_AGENT = "REx command line client v5.38.1224";
  private static final String UTF_8 = "UTF-8";
  private static final Collection<String> VALUE_OPTIONS =
    new HashSet<String>(Arrays.asList(new String[]{"-a", "-ll", "-name", "-interface"}));

  /**
   * Main program, containing most of the processing logic.
   *
   * @param args the command line arguments.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    final String BOUNDARY = "b-o-u-n-d-a-r-y";
    final String CRLF = "\r\n";

    HttpURLConnection connection = (HttpURLConnection) new URL(REX_URL).openConnection();
    connection.setRequestMethod("POST");
    connection.addRequestProperty("User-Agent", USER_AGENT);
    connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    connection.setDoOutput(true);

    OutputStream upload = connection.getOutputStream();
    Calendar date = new GregorianCalendar();

    write(upload, "--" + BOUNDARY + CRLF);
    write(upload, "Content-Disposition: form-data; name=\"tz\"" + CRLF);
    write(upload, CRLF);
    write(upload, Integer.toString((date.get(Calendar.ZONE_OFFSET) + date.get(Calendar.DST_OFFSET)) / (-60000)));
    write(upload, CRLF);

    write(upload, "--" + BOUNDARY + CRLF);
    write(upload, "Content-Disposition: form-data; name=\"command\"" + CRLF);
    write(upload, CRLF);
    write(upload, commandLine(args));
    write(upload, CRLF);

    File[] input = inputFiles(args);
    switch (input.length)
    {
    case 0:
      break;

    case 1:
      write(upload, "--" + BOUNDARY + CRLF);
      write(upload, "Content-Disposition: form-data; name=\"input\"; filename=\"" + input[0].getName() + "\"" + CRLF);
      write(upload, "Content-Type: text/plain" + CRLF);
      write(upload, CRLF);
      write(upload, read(input[0]));
      write(upload, CRLF);
      break;

    default:
      System.err.println("more than one input file specification");
      System.exit(1);
    }

    write(upload, "--" + BOUNDARY + "--" + CRLF);

    String rexStatus = connection.getHeaderField("REx-Status");
    String contentDisposition = connection.getHeaderField("Content-Disposition");
    String contentEncoding = connection.getHeaderField("Content-Encoding");
    InputStream download = contentEncoding != null && contentEncoding.equals("gzip")
                         ? new GZIPInputStream(connection.getInputStream())
                         : connection.getInputStream();

    final String attachment = "attachment; filename=";
    OutputStream output = System.out;
    if (contentDisposition != null && contentDisposition.startsWith(attachment))
    {
      output = new FileOutputStream(contentDisposition.substring(attachment.length()));
    }

    byte[] buffer = new byte[32768];
    for (int count; (count = download.read(buffer)) >= 0; )
    {
      output.write(buffer, 0, count);
    }

    output.close();
    download.close();

    System.exit(Integer.parseInt(rexStatus));
  }

  /**
   * Write a utf-8 encoded string to an output stream.
   *
   * @param os the OutputStream to write to.
   * @param s the string to write.
   * @throws Exception
   */
  private static void write(OutputStream os, String s) throws Exception
  {
    os.write(s.getBytes(UTF_8));
  }

  /**
   * Reconstruct a command line from Java main-style args. Omit any
   * arguments deemed to be input file names. An input file name is
   * identified by the fact that is does not follow a command option
   * that is known to have a value, and an existing file in the file
   * system.
   *
   * @param args the command line arguments.
   * @return the reconstructed command line.
   */
  private static String commandLine(String args[])
  {
    StringBuilder commandLine = new StringBuilder();
    String delimiter = "";
    boolean skip = false;
    for (String arg : args)
    {
      if (skip || arg.startsWith("-") || ! new File(arg).exists())
      {
        commandLine.append(delimiter);
        delimiter = " ";
        arg = arg.replaceAll("\"", "\\\"");
        if (arg.isEmpty() || arg.indexOf(' ') >= 0)
        {
          commandLine.append('"');
          commandLine.append(arg);
          commandLine.append('"');
        }
        else
        {
          commandLine.append(arg);
        }
      }
      skip = ! skip && VALUE_OPTIONS.contains(arg);
    }
    return commandLine.toString();
  }

  /**
   * Extract input file arguments from command line. An input file name
   * is identified by the fact that is does not follow a command option
   * that is known to have a value, and an existing file in the file
   * system.
   *
   * @param args the command line arguments.
   * @return an array of File objects.
   */
  private static File[] inputFiles(String args[])
  {
    Collection<File> files = new ArrayList<File>();
    boolean skip = false;
    for (String arg : args)
    {
      if (! skip && ! arg.startsWith("-"))
      {
        File file = new File(arg);
        if (file.exists())
        {
          files.add(file);
        }
      }
      skip = ! skip && VALUE_OPTIONS.contains(arg);
    }
    return files.toArray(new File[0]);
  }

  /**
   * Read a utf-8 encoded file and return its contents as a
   * string. Strip leading byte order mark, if present.
   *
   * @param input the input file.
   * @return the file contents.
   * @throws Exception
   */
  private static String read(File input) throws Exception
  {
    byte buffer[] = new byte[(int) input.length()];
    InputStream stream = new FileInputStream(input);
    stream.read(buffer);
    stream.close();
    String content = new String(buffer, UTF_8);
    return content.length() > 0 && content.charAt(0) == '\uFEFF'
         ? content.substring(1)
         : content;
  }
}
