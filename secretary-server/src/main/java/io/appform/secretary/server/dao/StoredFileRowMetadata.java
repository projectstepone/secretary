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
@Table(name = "transient_raw_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
                @UniqueConstraint(columnNames = {"file_id", "file_index"})}
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoredFileRowMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "partition_id")
    private int partitionId;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "file_index")
    private long fileIndex;

    @LookupKey
    @Column(name = "lookup_key")
    private String key;

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
