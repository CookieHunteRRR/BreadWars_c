package me.cookiehunterrr.breadwars.classes.customitems.evo;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class EvoDataType implements PersistentDataType<byte[], EvoItemInformation>
{
    @Override
    public Class<byte[]> getPrimitiveType()
    {
        return byte[].class;
    }

    @Override
    public Class<EvoItemInformation> getComplexType()
    {
        return EvoItemInformation.class;
    }

    @Override
    public byte[] toPrimitive(EvoItemInformation complex, PersistentDataAdapterContext context)
    {
        return SerializationUtils.serialize(complex);
    }

    @Override
    public EvoItemInformation fromPrimitive(byte[] primitive, PersistentDataAdapterContext context)
    {
        try {
            InputStream is = new ByteArrayInputStream(primitive);
            ObjectInputStream o = new ObjectInputStream(is);
            return (EvoItemInformation) o.readObject();
        }
        catch (IOException | ClassNotFoundException ex) { ex.printStackTrace(); }
        return null;
    }
}
