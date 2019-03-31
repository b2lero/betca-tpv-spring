package es.upm.miw.business_controllers;

import es.upm.miw.documents.Article;
import es.upm.miw.documents.Order;
import es.upm.miw.exceptions.BadRequestException;
import es.upm.miw.repositories.ArticleRepository;
import es.upm.miw.repositories.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import es.upm.miw.documents.OrderLine;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;
import org.springframework.stereotype.Controller;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ArticleRepository articleRepository;

    public Order closeOrder(String orderId, OrderLine[] orderLine) {
        Order closeOrder = orderRepository.findById(orderId).orElse(null);
        LogManager.getLogger().debug("Length: " + orderLine.length);
        if(orderLine.length > 0) {
            closeOrder.close();
            closeOrder.setOrderLines(orderLine);
            updateArticleStock(closeOrder);
            closeOrder = orderRepository.save(closeOrder);
        } else {
            throw new BadRequestException("orderLine is empty");
        }

        return closeOrder;
    }

    private void updateArticleStock(@NotNull Order order) {
        
        OrderLine[] orderLine = order.getOrderLines(); 
                
        for (OrderLine orderLineSingle : orderLine) {
            Article article = orderLineSingle.getArticle();
            Article articleDB = this.articleRepository.findById(article.getCode()).get();

            if(article.getStock() < 0) {
                // TODO: Notification all tickets 3/30/2019
            } else {
                articleDB.setStock(articleDB.getStock() + orderLineSingle.getFinalAmount());
                articleRepository.save(articleDB);
            }
        }
    }

    private List<OrderSearchDto> orderSearchDtos;

    public static String SEARCHWORD = "";

    public List<OrderSearchDto> readAll() {
        orderSearchDtos = new ArrayList<>();
        for (OrderDto dto : orderRepository.findAllOrders()) {
            for (OrderLine orderLine : dto.getOrderLines()) {
                createAddOrderSearchDto(dto, orderLine);
            }
        }
        return orderSearchDtos;
    }

    public List<OrderSearchDto> searchOrder(String orderDescription, String articleDescription, Boolean onlyClosingDate) {
        SEARCHWORD = (orderDescription).trim().toLowerCase().toString() + " " + (articleDescription).trim().toLowerCase().toString();
        orderSearchDtos = new ArrayList<>();
        for (OrderDto dto : orderRepository.findAllOrders())
            for (OrderLine orderLine : dto.getOrderLines()) {
                String orderDescry = dto.getDescription().toLowerCase();
                String articleDescry = orderLine.getArticle().getDescription().toLowerCase();
                if (orderDescription == "" && articleDescription == "") {
                    validateClosingDate(dto, orderLine, onlyClosingDate);
                } else if (SEARCHWORD.contains(orderDescry) || SEARCHWORD.contains(articleDescry)) {
                    validateClosingDate(dto, orderLine, onlyClosingDate);
                }
            }
        return orderSearchDtos;
    }

    public void validateClosingDate(OrderDto dto, OrderLine orderLine, Boolean onlyClosingDate) {
        if (onlyClosingDate == false)
            createAddOrderSearchDto(dto, orderLine);
        else if (dto.getClosingDate() != null)
            createAddOrderSearchDto(dto, orderLine);
    }

    public void createAddOrderSearchDto(OrderDto dto, OrderLine orderLine) {
        OrderSearchDto orderSearchDto;
        orderSearchDto = new OrderSearchDto(dto.getDescription(), orderLine.getArticle().getDescription(), orderLine.getRequiredAmount(), orderLine.getFinalAmount(), dto.getOpeningDate(), dto.getClosingDate());
        orderSearchDtos.add(orderSearchDto);
    }
}
