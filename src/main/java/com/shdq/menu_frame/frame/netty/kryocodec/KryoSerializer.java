package com.shdq.menu_frame.frame.netty.kryocodec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author shdq-fjy
 */
public class KryoSerializer {
    private static Kryo kryo = KryoFactory.createKryo();

    public static void serialize(Object o, ByteBuf out){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output,o);
        output.flush();
        output.close();
        byte[] bytes = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.writeBytes(bytes);
    }

    public static Object deserialize(ByteBuf in){
        if (in == null){
            return null;
        }
        Input input = new Input(new ByteBufInputStream(in));
        Object o = kryo.readClassAndObject(input);
        input.close();
        return o;
    }
}
