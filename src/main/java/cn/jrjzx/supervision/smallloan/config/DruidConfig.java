package cn.jrjzx.supervision.smallloan.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@Configuration
@ConditionalOnProperty(prefix="druid",name="monitor",havingValue="true")
public class DruidConfig { 
  @Bean 
  public ServletRegistrationBean druidServlet() { 
    ServletRegistrationBean reg = new ServletRegistrationBean(); 
    reg.setServlet(new StatViewServlet()); 
    reg.addUrlMappings("/druid/*"); 
    //reg.addInitParameter("allow", "127.0.0.1"); //白名单 
    //reg.addInitParameter("deny",""); //黑名单 
    reg.addInitParameter("loginUsername", "admin"); 
    reg.addInitParameter("loginPassword", "admin"); 
    return reg; 
  } 

  @Bean public FilterRegistrationBean filterRegistrationBean() { 
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(); 
    filterRegistrationBean.setFilter(new WebStatFilter()); 
    filterRegistrationBean.addUrlPatterns("/*"); 
    filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"); 
    return filterRegistrationBean; 
   }
}