package com.reviewservice.mapper;

import com.reviewservice.dto.ReportDto;
import com.reviewservice.dto.ReportStatusDto;
import com.reviewservice.entity.Report;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportStatusDto toReportStatusDto(Report report);

    List<ReportStatusDto> toReportStatusDtoList(List<Report> reports);

    ReportDto toReportDto(Report report);

    List<ReportDto> toReportDtoList(List<Report> reports);
}
