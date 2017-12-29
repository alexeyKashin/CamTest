package com.company.camtest.web.screens;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.FileDataProvider;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Upload;
import org.apache.commons.compress.utils.IOUtils;
import org.vaadin.teemu.webcam.Webcam;
import com.vaadin.server.FileResource;

import javax.inject.Inject;
import java.io.*;
import java.util.Map;

public class Screen extends AbstractWindow {

    @Inject
    private VBoxLayout box;

    private File targetFile;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private ExportDisplay exportDisplay;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Webcam webcam = new Webcam();

        Layout layout = (Layout) WebComponentsHelper.unwrap(box);

        webcam.setWidth("400px");
        webcam.setReceiver(new Upload.Receiver() {

            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
                try {
                    targetFile = File.createTempFile(filename, ".jpeg");
                    targetFile.deleteOnExit();
                    return new FileOutputStream(targetFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        webcam.addCaptureSucceededListener(new Webcam.CaptureSucceededListener() {

            @Override
            public void captureSucceeded(Webcam.CaptureSucceededEvent event) {
                /*Image img = new Image("Captured image", new FileResource(targetFile));
                img.setWidth("200px");
                layout.addComponent(img);*/
                try {
                    exportDisplay.show(new ByteArrayDataProvider(IOUtils.toByteArray(new FileInputStream(targetFile)))
                            , "1.jpeg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Add a button as an alternative way to capture.
        Button button = componentsFactory.createComponent(Button.class);
        button.setAction(new AbstractAction("123") {
            @Override
            public void actionPerform(Component component) {
                webcam.capture();
            }
        });
        layout.addComponent(webcam);
        //box.add(button);
    }
}