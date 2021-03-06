package erdemc.deneme.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Part;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import erdemc.deneme.amqp.listener.DenemeQueueListener;
import erdemc.deneme.exception.UserNotFoundException;
import erdemc.deneme.model.User;
import erdemc.deneme.service.OrderService;
import erdemc.deneme.service.UsersService;

@Controller
public class LoginController {
	
	private RabbitTemplate rabbitTemplate;
	
	private UsersService usersService;
	
	private OrderService orderService;

	@Autowired
	public LoginController(RabbitTemplate rabbitTemplate, UsersService usersService, OrderService orderService) {
		this.rabbitTemplate = rabbitTemplate;
		this.usersService = usersService;
		this.orderService = orderService;
	}

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String displayLogin() {
		System.out.println(usersService.findAll());
		System.out.println(orderService.findAll());
		return "login";
	}
	
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public String displayLogin(@RequestPart("profilePicture") Part mFile) {
		try {
			mFile.write("/home/superdem/Downloads/a.jpg");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}
	
	@RequestMapping(value = "/users/json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<User> findUsers() {
		return usersService.findAll();
	}
	
	@RequestMapping(value="/users/json/{userCode}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User throwException(@PathVariable String userCode) throws UserNotFoundException {
		User user = usersService.findUser(userCode);
		if(user == null) {
			throw new UserNotFoundException(userCode);
		} else {
			return user;
		}
	}
	
	@RequestMapping(value="/amqp/{message}", method = RequestMethod.GET)
	public String sendAMQPMessage(@PathVariable String message) {
		rabbitTemplate.convertAndSend(DenemeQueueListener.DENEME_QUEUE, message);
		return "login";
	}
	
}

