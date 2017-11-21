package kr.zany.sample.netty4.server.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.inject.Named;

/**
 * <b>Application Base Settings</b>
 * <p>Yaml Configuration Value Object (app 하위 설정)</p>
 *
 * <p>Copyright ⓒ 2015 kt corp. All rights reserved.</p>
 *
 * @author Lee Sang-Hyun (zanylove@gmail.com)
 * @since 2015. 10. 01.
 */
@Data
@Named
@ConfigurationProperties(prefix = "app")
public class AppBaseSettings {

    private String name;
    private String fullName;
    
    private String version;
    private String basePackage;
}
