/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.pool2.BaseObjectPool;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Alejandro
 */
public class PoolTest {

    ObjectPool<Connection> pool;
    public static final String pwd = "9AE7xst0iD";
    long time_start, time_end;

    @Test(expectedExceptions = org.postgresql.util.PSQLException.class,
            expectedExceptionsMessageRegExp = ".*too many connections.*"
    )
    public void soloDebeCrear5Conexiones() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo2_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            pool.borrowObject();
        }
    }

    @Test
    public void aprendiendoAControlarLasConexiones() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo2_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            Connection c = pool.borrowObject();
            pool.returnObject(c);
        }
    }

    @Test(expectedExceptions = org.postgresql.util.PSQLException.class,
            expectedExceptionsMessageRegExp = ".*close.*"
    )

    public void quePasaCuandoSeCierraUnaConexionAntesDeRetornarla() throws Exception {

        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo2_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c = pool.borrowObject();
        c.close();
        pool.returnObject(c);
        Assert.assertTrue(c.createStatement().execute("Select 1"));
        // pool = fc.makeObject();
        Assert.assertTrue(pool.getNumActive() == 1);
    }

    @Test(expectedExceptions = java.lang.AssertionError.class)
    public void quePasaCuandoSeRetornaUnaconexionContransaccionIniciada() throws Exception {

       
        Connection c = pool.borrowObject();
        c.setAutoCommit(false);
        c.createStatement().execute("Insert into prueba values('nombres')");
        c.createStatement().execute("Insert into prueba values('nombres')");
        // c.commit();
        pool.returnObject(c);
        c.createStatement().execute("Insert into prueba values('sergio')");
        ResultSet rs = c.createStatement().executeQuery("Select * from prueba where nombre = 'sergio'");
        Assert.assertTrue(rs.wasNull());
    }

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConSingleton() throws Exception {

        time_start = System.currentTimeMillis();
        Connection cx = SingletonConnection.getConnection();
        for (int i = 0; i < 200; i++) {
            cx.createStatement().execute("Insert into hilos values('" + Thread.currentThread().getName() + "," + i + "')");
        }
        time_end = System.currentTimeMillis();
        System.out.println("#\n " + Thread.currentThread().getName() + " Duracion: " + (time_end - time_start) + " milliseconds");

    }

    @BeforeClass
    public void iniciarPool() {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo2_5", pwd);
        pool = new GenericObjectPool<Connection>(fc);
    }

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConObjectPool() throws Exception {

        time_start = System.currentTimeMillis();
        Connection c = pool.borrowObject();
        for (int i = 0; i < 200; i++) {
            c.createStatement().execute("Insert into prueba values('" + Thread.currentThread().getName() + " - " + i + "')");
        }
        time_end = System.currentTimeMillis();
        System.out.println("# " + Thread.currentThread().getName() + "\n  Duracion: " + (time_end - time_start) + " milliseconds");
    pool.returnObject(c);
    }
}
