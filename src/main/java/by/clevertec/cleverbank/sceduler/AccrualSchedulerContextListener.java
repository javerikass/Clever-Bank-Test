package by.clevertec.cleverbank.sceduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AccrualSchedulerContextListener implements ServletContextListener {

  private AccrualScheduler scheduler;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    scheduler = new AccrualScheduler();
    scheduler.startScheduler();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    scheduler.stopScheduler();
  }
}

