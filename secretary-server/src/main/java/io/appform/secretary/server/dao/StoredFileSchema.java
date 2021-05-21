package io.appform.secretary.server.dao;

import io.appform.dropwizard.sharding.sharding.LookupKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "file_schema",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
                @UniqueConstraint(columnNames = {"workflow"})}
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredFileSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @LookupKey
    @Column(name = "workflow")
    private String workflow;

    @Column(name = "validation_schema")
    private String schema;

    @Column(name = "created",
            columnDefinition = "timestamp",
            updatable = false,
            insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private Date created;

    @Column(name = "updated",
            columnDefinition = "timestamp default current_timestamp",
            updatable = false,
            insertable = false)
    private Date updated;
}
