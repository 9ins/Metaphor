package org.chaostocosmos.metadata.metaphor.api;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * MetaphorAPI
 * 
 * @author Kooin-Shin
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class MetaphorAPI {
    /**
     * Application context
     */
    private static ConfigurableApplicationContext applicationContext;    

    /**
     * Get application context
     * @return
     */
    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
   
    /**
     * Start metaphor API server
     * @param args
     * @throws IOException
     */
    public static void start(String[] args) throws IOException {
        SpringApplication springApplication = new SpringApplication(MetaphorAPI.class);                        
        applicationContext = springApplication.run(args);
    }

    public static void main(String[] args) throws IOException {
        start(args);
    }

    // /**
    //  * Apply custom configuration
    //  * @return
    //  * @throws IOException
    //  */
    // private static Map<String, Object> applyCustomConfiguration() throws IOException {
    //     ClassPathResource resource = new ClassPathResource("metaphor.yaml");
    //     List<PropertySource<?>> props = new YamlPropertySourceLoader().load("metaphor", resource);        
    //     for(PropertySource<?> source : props) {
    //         if(source.getName().equals("metaphor")) {
    //             Map<String, Object> prop = new HashMap<>();
    //             Utils.convertMapToProperties("metaphor", (Map<String, Object>) source.getSource(), prop);                
    //             prop.entrySet().stream().forEach(e -> System.out.println(e.getKey()+" ---------------- "+e.getValue()));
    //             prop = prop.entrySet().stream().map(e -> {
    //                                         if(e.getKey().startsWith("metaphor.server") || e.getKey().contains("metaphor.spring")) {
    //                                             return new AbstractMap.SimpleEntry<String, Object>(e.getKey().replace("metaphor.", ""), e.getValue());
    //                                         } else if(e.getKey().startsWith("metaphor.banner.location")) {
    //                                             return new AbstractMap.SimpleEntry<String, Object>(e.getKey().replace("metaphor", "spring"), e.getValue());
    //                                         }
    //                                         return e;
    //                                     }).collect(Collectors.toMap(e -> (String)e.getKey(), v -> v.getValue()));
    //             return prop;
    //         }
    //     }
    //     return null;
    // }

    // @Component
    // public static class CustomContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
    //     @Override
    //     public void onApplicationEvent(ContextRefreshedEvent event) {
    //         Environment env = event.getApplicationContext().getEnvironment();
    //         // Custom logic to execute when the context is refreshed
    //         System.out.println("Application Context is refreshed!");
    //     }
    // }    

    // @Component
    // public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    //     @Override
    //      public void initialize(ConfigurableApplicationContext applicationContext) {
    //          Map<String, Object> properties = applicationContext.getEnvironment().getSystemProperties();
    //          System.out.println(properties.toString());
    //          properties.put("spring.config.location", "classpath:metaphor.yaml");
    //          properties.put("application.version", properties.get("metaphor.version"));
    //          properties.put("spring.banner.location", properties.get("metaphor.banner.location"));
    //          properties.put("spring.security.user", properties.get("metaphor.security.user"));
    //      }    
    //  }    
}
