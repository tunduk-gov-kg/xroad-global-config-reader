package kg.gov.tunduk.xroad.global_configuration;

import kg.gov.tunduk.xroad.global_configuration.model.SharedParams;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultGlobalConfigurationProxy implements GlobalConfigurationProxy {

    public SharedParams getSharedParams(URL centralServerUrl) throws IOException, JAXBException {
        URL internalConfUrl = new URL(centralServerUrl, "internalconf");
        HttpURLConnection httpURLConnection = (HttpURLConnection) internalConfUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");
        InputStream connectionInputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connectionInputStream));
        String line;
        URL sharedParamsUrl = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("shared-params.xml")) {
                int separatorIndex = line.lastIndexOf(":");
                String path = line.substring(separatorIndex + 1).trim();
                sharedParamsUrl = new URL(centralServerUrl, path);
            }
        }
        if (sharedParamsUrl == null) {
            throw new IOException("No shared params url found");
        }
        HttpURLConnection sharedParamsUrlHttpConnection = (HttpURLConnection) sharedParamsUrl.openConnection();
        sharedParamsUrlHttpConnection.setRequestMethod("GET");
        InputStream inputStream = sharedParamsUrlHttpConnection.getInputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance(SharedParams.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (SharedParams) unmarshaller.unmarshal(inputStream);
    }

}
