package com.jvpereira.doclens.controller;

import com.jvpereira.doclens.dto.FieldRequestDTO;
import com.jvpereira.doclens.dto.TemplateRequestDTO;
import com.jvpereira.doclens.dto.TemplateResponseDTO;
import com.jvpereira.doclens.model.field.FieldType;
import com.jvpereira.doclens.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateViewController {

    private final TemplateService templateService;

    @GetMapping
    public String listTemplates(Model model) {
        model.addAttribute("templates", templateService.findAll());
        return "templates/list-templates";
    }

    @GetMapping("/new")
    public String newTemplateForm(Model model) {
        model.addAttribute("template", new TemplateRequestDTO());
        model.addAttribute("fieldTypes", FieldType.values());
        return "templates/form-template";
    }

    @PostMapping
    public String createTemplate(@ModelAttribute("template") TemplateRequestDTO templateRequestDTO) {
        templateService.create(templateRequestDTO);
        return "redirect:/templates";
    }

    @GetMapping("/edit/{id}")
    public String editTemplateForm(@PathVariable("id") UUID id, Model model) {
        TemplateResponseDTO template = templateService.findById(id);

        List<FieldRequestDTO> fieldRequests = new ArrayList<>();
        if (template.getFields() != null) {
            fieldRequests = template.getFields().stream()
                    .map(f -> FieldRequestDTO.builder()
                            .code(f.getCode())
                            .name(f.getName())
                            .description(f.getDescription())
                            .required(f.getRequired())
                            .type(f.getType())
                            .templateId(template.getId())
                            .parentFieldCode(f.getParentFieldCode())
                            .build())
                    .collect(Collectors.toList());
        }

        TemplateRequestDTO requestDTO = TemplateRequestDTO.builder()
                .tag(template.getTag())
                .name(template.getName())
                .description(template.getDescription())
                .propHints(template.getPropHints())
                .status(template.getStatus())
                .fields(fieldRequests)
                .build();

        model.addAttribute("template", requestDTO);
        model.addAttribute("templateId", id);
        model.addAttribute("fieldTypes", FieldType.values());
        return "templates/form-template";
    }

    @PostMapping("/edit/{id}")
    public String updateTemplate(@PathVariable("id") UUID id, @ModelAttribute("template") TemplateRequestDTO templateRequestDTO) {
        templateService.update(id, templateRequestDTO);
        return "redirect:/templates";
    }

    @PostMapping("/delete/{id}")
    public String deleteTemplate(@PathVariable("id") UUID id) {
        templateService.delete(id);
        return "redirect:/templates";
    }
}
