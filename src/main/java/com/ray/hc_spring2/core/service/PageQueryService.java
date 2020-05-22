package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.repository.AlarmLogRepository;
import com.ray.hc_spring2.core.repository.OperationLogRepository;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.OperationLog;
import com.ray.hc_spring2.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PageQueryService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private AlarmLogRepository alarmLogRepository;

    public Page<OperationLog> queryOperationLog(int page , String username, Date startTime, Date endTime){
        Pageable pageable = new PageRequest(page-1, Constants.PAGE_SIZE); //页码：前端从1开始，jpa从0开始，做个转换
        Specification<OperationLog> operationLogSpecification = getSpecification(username,startTime,endTime);
        return this.operationLogRepository.findAll(operationLogSpecification,pageable);
    }


    private Specification<OperationLog> getSpecification(String username, Date startTime, Date endTime){
        String startDate = DateUtil.formatDate(startTime);
        String endDate = DateUtil.formatDate(endTime);
        Specification<OperationLog> specification = new Specification<OperationLog>() {
            @Override
            public Predicate toPredicate(Root<OperationLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>(); //所有的断言
                if(StringUtils.isNotEmpty(username)){
                    predicates.add(cb.equal(root.get("user"), username));
                }
                if(StringUtils.isNotEmpty(startDate)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("generationTime").as(String.class), startDate));
                }
                if(StringUtils.isNotEmpty(endDate)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("generationTime").as(String.class), endDate));
                }
                query.orderBy(cb.desc(root.get("id")));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
        return specification;
    }


    public Page<AlarmLog> queryAlarmLog(int page , String state, Date startTime, Date endTime){
        Pageable pageable = new PageRequest(page-1, Constants.PAGE_SIZE); //页码：前端从1开始，jpa从0开始，做个转换
        Specification<AlarmLog> alarmLogSpecification = getAlarmLogSpecification(state,startTime,endTime);
        return this.alarmLogRepository.findAll(alarmLogSpecification,pageable);
    }

    private Specification<AlarmLog> getAlarmLogSpecification(String state, Date startTime, Date endTime){
        String startDate = DateUtil.formatDate(startTime);
        String endDate = DateUtil.formatDate(endTime);
        Specification<AlarmLog> specification = new Specification<AlarmLog>() {
            @Override
            public Predicate toPredicate(Root<AlarmLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>(); //所有的断言
                if(StringUtils.isNotEmpty(state)){
                    predicates.add(cb.equal(root.get("state"), state));
                }
                if(StringUtils.isNotEmpty(startDate)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("alarmTime").as(String.class), startDate));
                }
                if(StringUtils.isNotEmpty(endDate)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("alarmTime").as(String.class), endDate));
                }
                query.orderBy(cb.desc(root.get("id")));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
        return specification;
    }
}
