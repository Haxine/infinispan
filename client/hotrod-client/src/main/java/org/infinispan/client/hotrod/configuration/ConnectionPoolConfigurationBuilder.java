package org.infinispan.client.hotrod.configuration;

import java.util.Properties;

import org.infinispan.client.hotrod.impl.TypedProperties;
import org.infinispan.commons.configuration.Builder;

/**
 * ConnectionPoolConfigurationBuilder. Specifies connection pooling properties for the HotRod client.
 *
 * @author Tristan Tarrant
 * @since 5.3
 */
public class ConnectionPoolConfigurationBuilder extends AbstractConfigurationChildBuilder implements Builder<ConnectionPoolConfiguration> {
   private ExhaustedAction exhaustedAction = ExhaustedAction.WAIT;
   private boolean lifo = true;
   private int maxActive = -1;
   private int maxTotal = -1;
   private long maxWait = -1;
   private int maxIdle = -1;
   private int minIdle = 1;
   private long timeBetweenEvictionRuns = 120000;
   private long minEvictableIdleTime = 1800000;
   private int numTestsPerEvictionRun = 3;
   private boolean testOnBorrow = false;
   private boolean testOnReturn = false;
   private boolean testWhileIdle = true;

   ConnectionPoolConfigurationBuilder(ConfigurationBuilder builder) {
      super(builder);
   }

   /**
    * Specifies what happens when asking for a connection from a server's pool, and that pool is
    * exhausted.
    */
   public ConnectionPoolConfigurationBuilder exhaustedAction(ExhaustedAction exhaustedAction) {
      this.exhaustedAction = exhaustedAction;
      return this;
   }

   /**
    * Sets the LIFO status. True means that borrowObject returns the most recently used ("last in")
    * idle object in a pool (if there are idle instances available). False means that pools behave
    * as FIFO queues - objects are taken from idle object pools in the order that they are returned.
    * The default setting is true
    */
   public ConnectionPoolConfigurationBuilder lifo(boolean enabled) {
      this.lifo = enabled;
      return this;
   }

   /**
    * Controls the maximum number of connections per server that are allocated (checked out to
    * client threads, or idle in the pool) at one time. When non-positive, there is no limit to the
    * number of connections per server. When maxActive is reached, the connection pool for that
    * server is said to be exhausted. The default setting for this parameter is -1, i.e. there is no
    * limit.
    */
   public ConnectionPoolConfigurationBuilder maxActive(int maxActive) {
      this.maxActive = maxActive;
      return this;
   }

   /**
    * Sets a global limit on the number persistent connections that can be in circulation within the
    * combined set of servers. When non-positive, there is no limit to the total number of
    * persistent connections in circulation. When maxTotal is exceeded, all connections pools are
    * exhausted. The default setting for this parameter is -1 (no limit).
    */
   public ConnectionPoolConfigurationBuilder maxTotal(int maxTotal) {
      this.maxTotal = maxTotal;
      return this;
   }

   /**
    * The amount of time in milliseconds to wait for a connection to become available when the
    * exhausted action is {@link ExhaustedAction#WAIT}, after which a {@link java.util.NoSuchElementException}
    * will be thrown. If a negative value is supplied, the pool will block indefinitely.
    */
   public ConnectionPoolConfigurationBuilder maxWait(long maxWait) {
      this.maxWait = maxWait;
      return this;
   }

   /**
    * Controls the maximum number of idle persistent connections, per server, at any time. When
    * negative, there is no limit to the number of connections that may be idle per server. The
    * default setting for this parameter is -1.
    */
   public ConnectionPoolConfigurationBuilder maxIdle(int maxIdle) {
      this.maxIdle = maxIdle;
      return this;
   }

   /**
    * Sets a target value for the minimum number of idle connections (per server) that should always
    * be available. If this parameter is set to a positive number and timeBetweenEvictionRunsMillis
    * > 0, each time the idle connection eviction thread runs, it will try to create enough idle
    * instances so that there will be minIdle idle instances available for each server. The default
    * setting for this parameter is 1.
    */
   public ConnectionPoolConfigurationBuilder minIdle(int minIdle) {
      this.minIdle = minIdle;
      return this;
   }

   /**
    * Indicates the maximum number of connections to test during idle eviction runs. The default
    * setting is 3.
    */
   public ConnectionPoolConfigurationBuilder numTestsPerEvictionRun(int numTestsPerEvictionRun) {
      this.numTestsPerEvictionRun = numTestsPerEvictionRun;
      return this;
   }

   /**
    * Indicates how long the eviction thread should sleep before "runs" of examining idle
    * connections. When non-positive, no eviction thread will be launched. The default setting for
    * this parameter is 2 minutes.
    */
   public ConnectionPoolConfigurationBuilder timeBetweenEvictionRuns(long timeBetweenEvictionRuns) {
      this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
      return this;
   }

   /**
    * Specifies the minimum amount of time that an connection may sit idle in the pool before it is
    * eligible for eviction due to idle time. When non-positive, no connection will be dropped from
    * the pool due to idle time alone. This setting has no effect unless
    * timeBetweenEvictionRunsMillis > 0. The default setting for this parameter is 1800000(30
    * minutes).
    */
   public ConnectionPoolConfigurationBuilder minEvictableIdleTime(long minEvictableIdleTime) {
      this.minEvictableIdleTime = minEvictableIdleTime;
      return this;
   }

   /**
    * Indicates whether connections should be validated before being taken from the pool by sending
    * an TCP packet to the server. Connections that fail to validate will be dropped from the pool.
    * The default setting for this parameter is false.
    */
   public ConnectionPoolConfigurationBuilder testOnBorrow(boolean testOnBorrow) {
      this.testOnBorrow = testOnBorrow;
      return this;
   }

   /**
    * Indicates whether connections should be validated when being returned to the pool sending an
    * TCP packet to the server. Connections that fail to validate will be dropped from the pool. The
    * default setting for this parameter is false.
    */
   public ConnectionPoolConfigurationBuilder testOnReturn(boolean testOnReturn) {
      this.testOnReturn = testOnReturn;
      return this;
   }

   /**
    * Indicates whether or not idle connections should be validated by sending an TCP packet to the
    * server, during idle connection eviction runs. Connections that fail to validate will be
    * dropped from the pool. This setting has no effect unless timeBetweenEvictionRunsMillis > 0.
    * The default setting for this parameter is true.
    */
   public ConnectionPoolConfigurationBuilder testWhileIdle(boolean testWhileIdle) {
      this.testWhileIdle = testWhileIdle;
      return this;
   }

   /**
    * Configures the connection pool parameter according to properties
    */
   public ConnectionPoolConfigurationBuilder withPoolProperties(Properties properties) {
      TypedProperties typed = TypedProperties.toTypedProperties(properties);
      exhaustedAction(ExhaustedAction.values()[typed.getIntProperty("whenExhaustedAction", exhaustedAction.ordinal(), true)]);
      lifo(typed.getBooleanProperty("lifo", lifo, true));
      maxActive(typed.getIntProperty("maxActive", maxActive, true));
      maxTotal(typed.getIntProperty("maxTotal", maxTotal, true));
      maxWait(typed.getLongProperty("maxWait", maxWait, true));
      maxIdle(typed.getIntProperty("maxIdle", maxIdle, true));
      minIdle(typed.getIntProperty("minIdle", minIdle, true));
      numTestsPerEvictionRun(typed.getIntProperty("numTestsPerEvictionRun", numTestsPerEvictionRun, true));
      timeBetweenEvictionRuns(typed.getLongProperty("timeBetweenEvictionRunsMillis", timeBetweenEvictionRuns, true));
      minEvictableIdleTime(typed.getLongProperty("minEvictableIdleTimeMillis", minEvictableIdleTime, true));
      testOnBorrow(typed.getBooleanProperty("testOnBorrow", testOnBorrow, true));
      testOnReturn(typed.getBooleanProperty("testOnReturn", testOnReturn, true));
      testWhileIdle(typed.getBooleanProperty("testWhileIdle", testWhileIdle, true));
      return this;
   }

   @Override
   public void validate() {
   }

   @Override
   public ConnectionPoolConfiguration create() {
      return new ConnectionPoolConfiguration(exhaustedAction, lifo, maxActive, maxTotal, maxWait, maxIdle, minIdle, numTestsPerEvictionRun, timeBetweenEvictionRuns,
            minEvictableIdleTime, testOnBorrow, testOnReturn, testWhileIdle);
   }

   @Override
   public ConnectionPoolConfigurationBuilder read(ConnectionPoolConfiguration template) {
      exhaustedAction = template.exhaustedAction();
      lifo = template.lifo();
      maxActive = template.maxActive();
      maxTotal = template.maxTotal();
      maxWait = template.maxWait();
      maxIdle = template.maxIdle();
      minIdle = template.minIdle();
      numTestsPerEvictionRun = template.numTestsPerEvictionRun();
      timeBetweenEvictionRuns = template.timeBetweenEvictionRuns();
      minEvictableIdleTime = template.minEvictableIdleTime();
      testOnBorrow = template.testOnBorrow();
      testOnReturn = template.testOnReturn();
      testWhileIdle = template.testWhileIdle();
      return this;
   }

}
