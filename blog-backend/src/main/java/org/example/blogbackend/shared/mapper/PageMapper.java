package org.example.blogbackend.shared.mapper;

import org.example.blogbackend.shared.dto.PagedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PageMapper {

    // This generic method handles any type of Page<T>
    default <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        if (page == null) {
            return null;
        }
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
