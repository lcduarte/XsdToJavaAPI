package com.jeroenreijn.examples;

import com.jeroenreijn.examples.model.Presentation;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

public interface PresentationsRepository {

    Iterable<Presentation> findAll();

    Presentation findPresentation(Long id);

}