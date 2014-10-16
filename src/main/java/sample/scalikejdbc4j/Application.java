package sample.scalikejdbc4j;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import sample.dao.CompanyDao;
import sample.dao.ProgrammerDao;
import sample.scalikejdbc4j.entity.Company;
import sample.scalikejdbc4j.entity.Programmer;
import scalikejdbc.DataSourceConnectionPool;
import scalikejdbc4j.ConnectionPool;
import scalikejdbc4j.DB;
import scalikejdbc4j.GlobalSettings;
import scalikejdbc4j.globalsettings.LogLevel;
import scalikejdbc4j.globalsettings.LoggingSQLAndTimeSettings;

import javax.sql.DataSource;

@ComponentScan
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    static class ConnectionPoolSettings implements ApplicationListener<ApplicationContextEvent> {
        private final DataSource dataSource;

        @Autowired
        ConnectionPoolSettings(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void onApplicationEvent(ApplicationContextEvent event) {
            initDataSource();
            initQueryLogLevel();
        }

        private void initDataSource() {
            ConnectionPool.singleton(new DataSourceConnectionPool(dataSource));
        }

        private void initQueryLogLevel() {
            LoggingSQLAndTimeSettings loggingSettings = new LoggingSQLAndTimeSettings();
            loggingSettings.setLogLevel(LogLevel.INFO);
            GlobalSettings.setLoggingSQLAndTime(loggingSettings);
        }
    }

    @Configuration
    static class JacksonModuleConfiguration {
        @Bean
        Jdk8Module javaOptionalModule() {
            return new Jdk8Module();
        }
    }

    @RestController
    @RequestMapping("/companies")
    static class CompanyController {

        @RequestMapping(method = RequestMethod.GET)
        Page<Company> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int pageSize) {
            PageRequest pr = new PageRequest(page, pageSize);
            return DB.withReadOnlySession(session -> {
                CompanyDao dao = new CompanyDao(session);
                return new PageImpl<>(dao.findAll(pr), pr, dao.count());
            });
        }

        @RequestMapping(method = RequestMethod.POST)
        Company save(@RequestBody Company company) {
            return DB.withAutoCommitSession(session -> {
                return new CompanyDao(session).create(company.getName());
            });
        }

        @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
        void delete(@PathVariable Long id) {
            DB.withAutoCommitSession(session -> {
                new CompanyDao(session).delete(id);
                return id;
            });
        }
    }

    @RestController
    @RequestMapping("/programmers")
    static class ProgrammerController {

        @RequestMapping(method = RequestMethod.GET)
        Page<Programmer> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int pageSize) {
            PageRequest pr = new PageRequest(page, pageSize);
            return DB.withReadOnlySession(session -> {
                ProgrammerDao dao = new ProgrammerDao(session);
                return new PageImpl<>(dao.findAll(pr), pr, dao.count());
            });
        }
    }

}
