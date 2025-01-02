package xyz.atom7.recoveryweb.configs

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.atom7.recoveryweb.filters.ApiKeyFilter
import xyz.atom7.recoveryweb.filters.RateLimitFilter

@Configuration
class FiltersConfig
{

    @Bean
    fun rateLimitFilterRegistration(rateLimitFilter: RateLimitFilter): FilterRegistrationBean<RateLimitFilter>
    {
        val registrationBean = FilterRegistrationBean<RateLimitFilter>()

        registrationBean.filter = rateLimitFilter
        registrationBean.addUrlPatterns("*")

        return registrationBean
    }

    @Bean
    fun apiKeyFilterRegistration(apiKeyFilter: ApiKeyFilter): FilterRegistrationBean<ApiKeyFilter>
    {
        val registrationBean = FilterRegistrationBean<ApiKeyFilter>()

        registrationBean.filter = apiKeyFilter
        registrationBean.addUrlPatterns("/code/*", "/player/*")

        return registrationBean
    }

}
