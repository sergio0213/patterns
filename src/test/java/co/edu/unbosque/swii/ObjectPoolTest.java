package co.edu.unbosque.swii;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.commons.pool2.PooledObject;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by alejandro on 4/09/15.
 */
public class ObjectPoolTest {
    
    public static final String pwd="9AE7xst0iD";

    @Test(threadPoolSize = 1, invocationCount = 1)
    public void probandoLaCreacionDeVariasConexiones() throws Exception {
        System.out.println(Thread.currentThread().getId());
        FabricaConexiones f=new FabricaConexiones("aretico.com",5432,"software_2","grupo2",pwd);

        PooledObject<Connection> poledCx=f.makeObject();
        Assert.assertTrue(poledCx.getObject().createStatement().execute("Select 1"));
        poledCx.getObject().close();
    }
    
    @Test(threadPoolSize = 1, invocationCount = 1)
    public void probandoLaDestruccionDelObjeto() throws Exception {
        System.out.println(Thread.currentThread().getId());
        FabricaConexiones f=new FabricaConexiones("aretico.com",5432,"software_2","grupo2",pwd);
        
        PooledObject<Connection> poledCx=f.makeObject();
        f.destroyObject(poledCx);
        Assert.assertTrue(poledCx.getObject().isClosed());
    }
    
    @Test(threadPoolSize = 1, invocationCount = 1)
    public void probandoLaValidacion() throws Exception {
        System.out.println(Thread.currentThread().getId());
        FabricaConexiones f=new FabricaConexiones("aretico.com",5432,"software_2","grupo2",pwd);

        PooledObject<Connection> poledCx=f.makeObject();
        Boolean valido=f.validateObject(poledCx);
        Assert.assertTrue(valido);
                poledCx.getObject().close();

    }
    
    
}
