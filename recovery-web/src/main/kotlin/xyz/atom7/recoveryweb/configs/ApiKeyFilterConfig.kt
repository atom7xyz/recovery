package xyz.atom7.recoveryweb.configs

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.atom7.recoveryweb.filters.ApiKeyFilter

@Configuration
class ApiKeyFilterConfig
{

    @Bean
    fun apiKeyFilterRegistration(apiKeyFilter: ApiKeyFilter): FilterRegistrationBean<ApiKeyFilter>
    {
        val registrationBean = FilterRegistrationBean<ApiKeyFilter>()

        registrationBean.filter = apiKeyFilter
        registrationBean.addUrlPatterns("/code/*", "/player/*")

        return registrationBean
    }

}
