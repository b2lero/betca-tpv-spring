package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.OrderController;
import es.upm.miw.documents.Article;
import es.upm.miw.documents.Order;
import es.upm.miw.documents.OrderLine;
import es.upm.miw.documents.Provider;
import es.upm.miw.repositories.ArticleRepository;
import es.upm.miw.repositories.OrderRepository;
import es.upm.miw.repositories.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import es.upm.miw.dtos.OrderSearchDto;
import es.upm.miw.dtos.OrderSearchInputDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import es.upm.miw.dtos.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static java.sql.JDBCType.NULL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@ApiTestConfig
public class OrderResourceIT {

    private Order order;

    private Provider provider;

    @Autowired
    private ProviderRepository providerRepository;

    private Article article;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private RestService restService;
    private OrderSearchDto existentOrder;

    @BeforeEach
    void before() {
        this.article = this.articleRepository.findAll().get(0);
        this.provider = this.providerRepository.findAll().get(0);
        OrderLine orderLine = new OrderLine(article, 10);
        OrderLine[] orderLines = {orderLine};
        this.order = new Order("Test Order", this.provider, orderLines);
        this.order = this.orderRepository.save(this.order);
    }

    @Test
    void testCloseOrder() {
        //Order closedOrder = orderController.closeOrder(this.order.getId(), this.order.getOrderLines());
        OrderDto orderDto = new OrderDto(this.order);
        OrderDto[] closedOrder = this.restService.loginAdmin()
                .restBuilder(new RestBuilder<OrderDto[]>()).clazz(OrderDto[].class)
                .path(OrderResource.ORDERS).path(OrderResource.CLOSE)
                .body(orderDto).post().build();
        assertNotNull(closedOrder[0].getClosingDate());
        this.orderRepository.delete(this.order);
    }

    @Test
    void testReadNotFound() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<OrderSearchDto[]>()).clazz(OrderSearchDto[].class)
                        .path(OrderResource.ORDERS).path("/non-existent-id")
                        .get().build());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testReadAll() {
        List<OrderSearchDto> orders = Arrays.asList(this.restService.loginAdmin()
                .restBuilder(new RestBuilder<OrderSearchDto[]>()).clazz(OrderSearchDto[].class)
                .path(OrderResource.ORDERS)
                .get().build());
        assertTrue(orders.size() >= 0);
    }

    @Test
    void testFindByAttributesLike() {
        OrderSearchInputDto orderSearchInputDto = new OrderSearchInputDto("null", "null", false);
        List<OrderSearchDto> activesSearch = Arrays.asList(this.restService.loginAdmin()
                .restBuilder(new RestBuilder<OrderSearchDto[]>()).clazz(OrderSearchDto[].class)
                .path(OrderResource.ORDERS).path(OrderResource.SEARCH).body(orderSearchInputDto)
                .post().build());
        assertTrue(activesSearch.size() >= 0);

    }
}
