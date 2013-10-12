package com.thingmagic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.UUID;

public class BluetoothTransportAndroid implements SerialTransport
{
    private AndroidBluetoothReflection bluetoothReflection=null;

    private Object btAdapter = null;
    private Object bluetoothSocket;
    private static OutputStream btOutputStream = null;
    private static InputStream btInputStream = null;

    private boolean opened = false;
    private String deviceName;


    /** Creates a new instance of SerialPort through bluetooth */
    public BluetoothTransportAndroid(String deviceName)
    {       
        if (btAdapter == null)
        {
            btAdapter = bluetoothReflection.getBluetoothAdapter();
        }
        if(deviceName.startsWith("/"))
        {
            this.deviceName = deviceName.substring(1);
        }
        else
        {
            this.deviceName = deviceName;
        }
    }

    public void open() throws ReaderException
    {
        if(!opened)
        {
            if(btAdapter == null)
            {
                throw new ReaderException("Bluetooth is not supported");
            }

            if(!bluetoothReflection.isBluetoothEnabled(btAdapter))
            {
                throw new ReaderException("Bluetooth is not enabled");
            }

            Object bluetoothDevice = bluetoothReflection.getRemoteDevice(btAdapter, deviceName);
            if (!bluetoothReflection.isBonded(bluetoothDevice))
            {
                throw new ReaderException("The specified address is not a paired Bluetooth device.");
            }
            try
            {
                shutdown();
                bluetoothSocket = bluetoothReflection.createBluetoothSocket(bluetoothDevice);
                bluetoothReflection.connectToBluetoothSocket(bluetoothSocket);

                btOutputStream = bluetoothReflection.getOutputStream(bluetoothSocket);
                btInputStream = bluetoothReflection.getInputStream(bluetoothSocket);
                opened = true;

            } catch (Exception ex) {
                shutdown();
                throw new ReaderException( "Failed to connect "+ex.getMessage());
            }
         }
    }
    /**
     * Disconnects from the connected Bluetooth device.
     */
    public  void shutdown()
    {
        if (bluetoothSocket != null)
        {
            try
            {
                if (btOutputStream != null)
                {
                    btOutputStream.close();
                }
                if (btInputStream != null)
                {
                    btInputStream.close();
                }
                bluetoothReflection.closeBluetoothSocket(bluetoothSocket);

            } catch (IOException Ioe) {
                 Ioe.printStackTrace();
            }
            bluetoothSocket = null;
        }

        btInputStream = null;
        btOutputStream = null;
        opened = false;

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
            if(btOutputStream == null)
            {
                    throw new ReaderException("Bluetooth Connecttion lost");
            }
            btOutputStream.write(message, offset, length);
        }catch(Exception ex){
            throw new ReaderException(ex.getMessage());
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
            if(btInputStream == null){
            	throw new IOException("Bluetooth Connecttion lost");
            }
            int responseWaitTime=0;
            while(btInputStream.available()<=0 && responseWaitTime < 2000 )
            {
                 Thread.sleep(1);
                // Repeat the loop for every 1 sec untill we received response.
                responseWaitTime++;
            }

           if (btInputStream.available() <= 0) {
                throw new IOException("No Response from reader");
            }
            responseWaitTime = 0;
            while (btInputStream.available() < length && responseWaitTime <timeoutMs) {
                Thread.sleep(1);
                // Repeat the loop for every 1 sec untill we received required
                // data
                responseWaitTime++;
            }
            if(btInputStream.available() < length){
                throw new IOException("No Response from reader");
            }
            btInputStream.read(messageSpace, offset, length);


        } catch (Exception ex) {
            shutdown();
            ex.printStackTrace();
            throw new ReaderException(ex.getMessage());
        }
        return messageSpace;
    }
}
