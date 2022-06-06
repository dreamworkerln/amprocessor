package ru.kvanttelecom.tv.cameradetails.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Prepare database (insert data) before running any tests
 */
@Component
@Slf4j
public class TestInitializer implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) {

        // create default users, that Authorisation server do
        //usersInitializer.initUsers();

        // Create additional users
        //initUsers();

        /*
        Address from;
        Address to;
        Order order;

        User user;
        Client client;
        Courier courier;

        // Prepare database here
        from = new Address("Москва", "Улица красных тюленей", 1, 2, 3);
        to = new Address("Мухосранск", "Западная", 2, 2, 5);

        user = new User("vasya", "INVALID",
                "Вася", "Пупкин", 55,"vasya@mail.ru", "1122334455");
        userService.save(user);

        //client = new Client(user, "CLIENT_DATA");
        //clientService.save(client);

        user = new User("sema", "INVALID",
                "Сема", "Пасечкин", 46, "sema@mail.ru", "908796786543");
        userService.save(user);

        //courier = new Courier(user, "COURIER_DATA");
        //courierService.save(courier);


        order = new Order();
        order.setFrom(from);
        order.setTo(to);
        order.setStatus(OrderStatus.COMPLETED);
        order.setCourier(courier);
        order.setClient(client);
        orderService.save(order);
        */

    }

//
//    private void initUsers() {
//
//        User user;
//        // vasya/vasya_password
//
//
//
//        if (userService.findByUsername("vasya").isEmpty()) {
//            user = new User("vasya", "{bcrypt}$2a$10$ptWulW3vFICm8Pu.CmulbuNx1GsgwO8UHrcZuVJi22mF792qRxjMu",
//                "Вася", "Пупкин", 37, "vasya@mail.ru", "1122334455");
//
//            userService.save(user);
//        }
//
//        // sema/sema_password
//        if (userService.findByUsername("sema").isEmpty()) {
//            user = new User("sema", "{bcrypt}$2a$10$zqdgSPaIehsb82r7psbBKOU5bkfCo8pqv9BwuwLz5BoEcSXQuqdnW",
//                "Сема", "Пасечкин",null, "sema@mail.ru", "908796786543");
//            userService.save(user);
//        }
//    }
}

