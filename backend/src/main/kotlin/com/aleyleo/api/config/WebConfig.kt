package com.aleyleo.api.config

import com.aleyleo.api.archivo.FileStorageService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

// Endpoint 27: GET /uploads/{archivo} sirve las imágenes guardadas en la carpeta "uploads".
@Configuration
class WebConfig(private val fileStorageService: FileStorageService) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(fileStorageService.raiz.toUri().toString())
    }
}
