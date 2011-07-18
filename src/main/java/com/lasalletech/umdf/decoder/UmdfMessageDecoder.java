package com.lasalletech.umdf.decoder;

import java.io.ByteArrayInputStream;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.template.TemplateRegistry;

public class UmdfMessageDecoder {
    /*private final UmdfStreamReader reader;
    private Thread parserThread;
    private boolean isRunning;
    private final TemplateRegistry templates;

    public UmdfMessageDecoder(UmdfStreamReader reader, TemplateRegistry templates) {
        this.reader = reader;
        this.templates = templates;
    }

    public void start() {
        parserThread = new Thread("UMDF Message Decoder " + reader.getStream()) {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                    	System.out.println("[UmdfMessageDecoder.parserThread.run]: waiting for message");
                        UmdfMessage umdfMessage = reader.next();
                        byte[] data = umdfMessage.getData();
                        //Message fastMessage = readMessage(data);
                        // TODO - what do we do with message?
                        //System.out.println(fastMessage);
                        System.out.println(new String(data));
                    } catch (InterruptedException e) {
                        break; // Stop called
                    } catch (Exception e) {
                    	e.printStackTrace();
                    	break;
                    }
                }
            }
        };
        isRunning = true;
        parserThread.start();
    }

    protected Message readMessage(byte[] data) {
        Context context = new Context();
        context.setTemplateRegistry(templates);
        FastDecoder decoder = new FastDecoder(context, new ByteArrayInputStream(data));
        return decoder.readMessage();
    }

    public void stop() {
        isRunning = false;
        parserThread.interrupt();
    }*/
}
