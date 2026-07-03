package com.example.presupuesto_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.presupuesto_service.controller.PresupuestoControllerV2;
import com.example.presupuesto_service.model.Presupuesto;

@Component
public class PresupuestoModelAssembler implements RepresentationModelAssembler<Presupuesto, EntityModel<Presupuesto>> {

    @Override
    public EntityModel<Presupuesto> toModel(Presupuesto presupuesto) {
        return EntityModel.of(presupuesto,
                linkTo(methodOn(PresupuestoControllerV2.class).obtenerPresupuesto(presupuesto.getId())).withSelfRel());
            }
}