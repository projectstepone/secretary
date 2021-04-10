package io.appform.secretary;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class App extends Application<AppConfig> {

    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "secretary";
    }

    @Override
    public void initialize(final Bootstrap<AppConfig> bootstrap) {
        // TODO: application initialization
    }


    @Override
    public void run(final AppConfig configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
