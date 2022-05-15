package com.activitibpmnjava.demo.io.repo;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.activitibpmnjava.demo.io.entity.ApplicationWorkflow;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AppWorkflowRepository extends PagingAndSortingRepository <ApplicationWorkflow, Long>, QuerydslPredicateExecutor<ApplicationWorkflow> {

    ApplicationWorkflow findByWfTaskId(String taskId);

}
