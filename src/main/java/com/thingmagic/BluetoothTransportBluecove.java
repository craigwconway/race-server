/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thingmagic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 *
 * @author qvantel
 */
public class BluetoothTransportBluecove implements SerialTransport{

    private static OutputStream btOutputStream = null;
    private static InputStream btInputStream = null;
    private boolean opened = false;
    private String deviceName;
    
 /** Creates a new instance of SerialPort through bluetooth */
    public BluetoothTransportBluecove(String deviceName)
    {
        if (deviceName.startsWith("/")) {
            this.deviceName = deviceName.substring(1);
        } else {
            this.deviceName = deviceName;
        }
    }
    public void open()
    {
        try
        {
            if(!opened)
            {
                String serverUrl="btspp://"+deviceName+":1;authenticate=false;encrypt=false;master=true";
                System.out.println("serverUrl :"+serverUrl);
                Class connectorClass;
                Class outputConnectorClass;
                Class inputConnectorClass;
                try
                {
                    connectorClass = Class.forName("javax.microedition.io.Connector");
                    outputConnectorClass = Class.forName("javax.microedition.io.OutputConnection");
                    inputConnectorClass = Class.forName("javax.microedition.io.InputConnection");                    
                }
                catch (ClassNotFoundException cnf)
                {
                    throw new Exception("Bluecove jar is not available in classpath");
                }

                Object connectorObj = BluecoveBluetoothReflection.connectToBluetoothSocket(connectorClass, serverUrl);
                btOutputStream=BluecoveBluetoothReflection.getOutputStream(outputConnectorClass, connectorObj);
                btInputStream=BluecoveBluetoothReflection.getInputStream(inputConnectorClass, connectorObj);
                opened = true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
              //  throw new Exception(ex.getMessage());
        }
    }

    public void shutdown() {
        try
        {
            btOutputStream.close();
            btInputStream.close();
            //conn.close();
            opened = false;
        }
        catch (IOException Ioe)
        {
            Ioe.printStackTrace();
        }
    }

    public void flush()
    {
        try
        {
            btOutputStream.flush();
        }
        catch (IOException Ioe)
        {
            Ioe.printStackTrace();
        }
    }

    int rate = 9600;

    public void setBaudRate(int rate) {
        // nativeSetBaudRate(rate);
    }

    public int getBaudRate() {
        return rate;
    }

    public void sendBytes(int length, byte[] message, int offset, int timeoutMs)
                    throws ReaderException
    {
        try
        {
            btOutputStream.write(message, offset, length);
        } catch (IOException Ioe) {
            throw new ReaderException(Ioe.getMessage());
        }
    }

    public byte[] receiveBytes(int length, byte[] messageSpace, int offset,
                    int timeoutMs) throws ReaderException
    {
        if (messageSpace == null)
        {
            messageSpace = new byte[length + offset];
        }
        try
        {
            int responseWaitTime=0;
            while(btInputStream.available()<=0 && responseWaitTime < 2000 )
            {
                 Thread.sleep(1);
                // Repeat the loop for every 1 sec untill we received response.
                responseWaitTime++;
            }
            
            if(btInputStream.available()<=0)
            {
                shutdown();
                throw new Exception("No Response from reader.");
            }
            while( btInputStream.available() < length )
            {
                Thread.sleep(1);
                // Repeat the loop for every 1 sec untill we received required data
            }
            btInputStream.read(messageSpace, offset, length);
         

        } catch (Exception ex) {
            ex.printStackTrace();;
            throw new ReaderException(ex.getMessage());
        }
        return messageSpace;
    }
}
