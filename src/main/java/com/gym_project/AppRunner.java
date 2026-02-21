package com.gym_project;

import com.gym_project.config.HibernateConfig;
import com.gym_project.entity.TrainingType;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class AppRunner {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(HibernateConfig.class);

        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();

//        em.getTransaction().begin();
//
//        TrainingType trainingType = new TrainingType();
//        trainingType.setTrainingTypeName("Cardio");
//
//        em.persist(trainingType);
//
//        em.getTransaction().commit();
//        em.close();
//        context.close();
//
//        System.out.println("Done!");
    }
}