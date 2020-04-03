package com.shdq.menu_frame.frame.netty.kryocodec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.shdq.menu_frame.frame.netty.vo.NettyHeader;
import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import de.javakaffee.kryoserializers.*;

import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class KryoFactory {

    public static Kryo createKryo(){
        Kryo kryo = new Kryo();
        //fixme:如果客户端和服务端不是在同一个应用需要这么设置，否则可以只设置 kryo.setRegistrationRequired(false);
        kryo.setRegistrationRequired(true);
        kryo.register(NettyMessage.class);
        kryo.register(NettyHeader.class);

        kryo.register(Arrays.asList("").getClass(),new ArraysAsListSerializer());
        kryo.register(GregorianCalendar.class,new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class,new JdkProxySerializer());
        kryo.register(BigDecimal.class,new DefaultSerializers.BigDecimalSerializer());
        kryo.register(BigInteger.class,new DefaultSerializers.BigIntegerSerializer());
        kryo.register(Pattern.class,new RegexSerializer());
        kryo.register(BitSet.class,new BitSetSerializer());
        kryo.register(URI.class,new URISerializer());
        kryo.register(UUID.class,new UUIDSerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeSet.class);
        kryo.register(Hashtable.class);
        kryo.register(Date.class);
        kryo.register(Calendar.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(StringBuffer.class);
        kryo.register(StringBuilder.class);
        kryo.register(Object.class);
        kryo.register(String.class);
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);
        return kryo;
    }
}
