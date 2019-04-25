/*
SQLyog v10.2 
MySQL - 5.7.21 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `user` (
	`id` int (11),
	`name` varchar (96),
	`password` varchar (96),
	`email` varchar (96),
	`phone_number` varchar (45),
	`status` int (2),
	`avatar` varchar (765),
	`create_time` datetime ,
	`last_login_time` datetime ,
	`last_update_time` datetime 
); 
insert into `user` (`id`, `name`, `password`, `email`, `phone_number`, `status`, `avatar`, `create_time`, `last_login_time`, `last_update_time`) values('1','wali','wali','wali@imooc.com','13888888888','1',NULL,'2018-09-25 18:08:39','2018-09-25 18:08:39','2018-09-25 18:08:39');
insert into `user` (`id`, `name`, `password`, `email`, `phone_number`, `status`, `avatar`, `create_time`, `last_login_time`, `last_update_time`) values('2','admin','55b3d0936a3fb63168d57a6bda0ddbbf','admin@imooc.com','13999999999','1',NULL,'2018-09-25 18:08:39','2018-09-25 18:08:39','2018-09-25 18:08:39');
