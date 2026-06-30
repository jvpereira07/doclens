package com.jvpereira.doclens.model.field;

import com.jvpereira.doclens.model.template.Template;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "fields", uniqueConstraints = @UniqueConstraint(columnNames = {"template_id", "code"}))
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name= "code", nullable = false)
    private String code;
    @Column(name= "name")
    private String name;
    @Column(name= "description", length = 1024)
    private String description;
    private Boolean required;
    @Enumerated(EnumType.STRING)
    private FieldType type;
    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;
}
