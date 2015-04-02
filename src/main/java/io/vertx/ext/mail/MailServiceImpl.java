package io.vertx.ext.mail;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

/**
 * MailService implementation for sending mails inside the local JVM
 *
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 *
 */
public class MailServiceImpl implements MailService {

  private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

  private Vertx vertx;
  private MailConfig config;
  private boolean stopped = false;

  private ConnectionPool connectionPool;

  /**
   * construct a MailService object with the vertx and config configuration
   * <p>
   * this is used by MailService.create
   * @param vertx the Vertx instance the mails will be sent in
   * @param config the configuration of the mailserver
   */
  public MailServiceImpl(Vertx vertx, MailConfig config) {
    if (connectionPool == null) {
      connectionPool = new ConnectionPool(vertx);
    }
    this.vertx = vertx;
    this.config = config;
  }

  @Override
  public void start() {
    // may take care of validating the options
    // and configure a queue if we implement one
    log.debug("mail service started");
  }

  @Override
  public void stop() {
    if (!stopped) {
      stopped = true;
      connectionPool.stop();
    }
    log.debug("mail service stopped");
  }

  @Override
  public MailService sendMail(MailMessage message, Handler<AsyncResult<JsonObject>> resultHandler) {
    vertx.runOnContext(v -> {
      MailMain mailMain = new MailMain(config, connectionPool, resultHandler);
      mailMain.sendMail(message);
    });
    return this;
  }

  @Override
  public MailService sendMailString(MailMessage message, String messageText,
      Handler<AsyncResult<JsonObject>> resultHandler) {
    vertx.runOnContext(v -> {
      MailMain mailMain = new MailMain(config, connectionPool, resultHandler);
      mailMain.sendMail(message, messageText);
    });
    return this;
  }

}
