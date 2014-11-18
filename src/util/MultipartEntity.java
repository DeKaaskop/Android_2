package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

/*
 * Follows the tutorial from http://www.codepool.biz/tech-frontier/android/take-a-photo-from-android-camera-and-upload-it-to-a-remote-php-server.html
 * Code is available on https://github.com/DynamsoftRD/JavaHTTPUpload  
 */
public class MultipartEntity implements HttpEntity
{
	// FIELDS
	private String boundary = null;

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isSetLast = false;
	boolean isSetFirst = false;

	// CONSTRUCTOR
	public MultipartEntity()
	{
		this.boundary = System.currentTimeMillis() + "";
	}

	// METHODS
	public void writeFirstBoundaryIfNeeds()
	{
		if (!isSetFirst)
		{
			try
			{
				out.write(("--" + boundary + "\r\n").getBytes());
			}
			catch (final IOException e)
			{

			}
		}
		isSetFirst = true;
	}

	public void writeLastBoundaryIfNeeds()
	{
		if (isSetLast)
		{
			return;
		}
		try
		{
			out.write(("\r\n--" + boundary + "--\r\n").getBytes());
		}
		catch (final IOException e)
		{

		}
		isSetLast = true;
	}

	public void addPart(final String key, final String value)
	{
		writeFirstBoundaryIfNeeds();
		try
		{
			out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
			out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
			out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
			out.write(value.getBytes());
			out.write(("\r\n--" + boundary + "\r\n").getBytes());
		}
		catch (final IOException e)
		{

		}
	}

	public void addPart(final String key, final String fileName,
			final InputStream fin)
	{
		addPart(key, fileName, fin, "application/octet-stream");
	}

	public void addPart(final String key, final String fileName,
			final InputStream fin, String type)
	{
		writeFirstBoundaryIfNeeds();
		try
		{
			type = "Content-Type: " + type + "\r\n";
			out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
			out.write(type.getBytes());
			out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

			final byte[] tmp = new byte[4096];
			int l = 0;
			while ((l = fin.read(tmp)) != -1)
			{
				out.write(tmp, 0, l);
			}
			out.flush();
		}
		catch (final IOException e)
		{

		}
		finally
		{
			try
			{
				fin.close();
			}
			catch (final IOException e)
			{

			}
		}
	}

	public void addPart(final String key, final File value)
	{
		try
		{
			addPart(key, value.getName(), new FileInputStream(value));
		}
		catch (final FileNotFoundException e)
		{

		}
	}

	@Override
	public long getContentLength()
	{
		writeLastBoundaryIfNeeds();
		return out.toByteArray().length;
	}

	@Override
	public Header getContentType()
	{
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	@Override
	public boolean isChunked()
	{
		return false;
	}

	@Override
	public boolean isRepeatable()
	{
		return false;
	}

	@Override
	public boolean isStreaming()
	{
		return false;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException
	{
		outstream.write(out.toByteArray());
	}

	@Override
	public Header getContentEncoding()
	{
		return null;
	}

	@Override
	public void consumeContent() throws IOException, UnsupportedOperationException
	{
		if (isStreaming())
		{
			throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
		}
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException
	{
		return new ByteArrayInputStream(out.toByteArray());
	}
}