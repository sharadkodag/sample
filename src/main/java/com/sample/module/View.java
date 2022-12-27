package com.sample.module;

import com.sample.entity.User;
import com.sample.mvputil.BaseView;
import com.sample.service.UserService;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Queue;

@Route("")
@SpringComponent
@UIScope
public class View extends BaseView<Presenter> {

    @Autowired
    Presenter presenter;

    @Autowired
    MessageController messageController;

    @Autowired
    MessageListener messageListener;
    @Autowired
    private JmsTemplate jmsTemplate;

    HorizontalLayout horizontalLayout;
    HorizontalLayout buttonLayout;
    Grid<User> userGrid;
    Button addButton;
    Button updateButton;
    Button deleteButton;
    IntegerField idField;
    TextField nameField;
    FormLayout formLayout;
    Button saveButton;
    Button cancelButton;
    Binder<User> userBinder;
    TextField messageField;
    Button sendMEssageButton;
    Button receiveButton;
    Grid<String> messageGrid;


    @Override
    protected void init() {
        setSizeFull();

        setButtonLayout();
        gridIntit();
        initFieldLayout();
        initBinder();
        Queue queue = new ArrayDeque();
        jmsTemplate.convertAndSend("queue","My message");
        String o = (String)jmsTemplate.receiveAndConvert("queue");
        System.out.println("Message : " + o);
    }

    public void setButtonLayout(){
        buttonLayout = new HorizontalLayout();
        addButton = new Button("Add");
        updateButton = new Button("Update");
        deleteButton = new Button("Delete");

        addButton.addClickListener(event -> {
            userBinder.setBean(new User());
            if(userGrid.getSelectedItems().size()==0) {
                formLayout.setEnabled(true);
            }else {
                Notification.show("Please deselect users");
            }
        });
        updateButton.addClickListener(event -> {
           if(userGrid.getSelectedItems().size()>0){
               formLayout.setEnabled(true);
               userBinder.setBean(userGrid.getSelectedItems().iterator().next());
           }else {
               Notification.show("Please select users");
           }
        });
        deleteButton.addClickListener(event -> {
            if(userGrid.getSelectedItems().size()>0){
                deleteUser(userGrid.getSelectedItems().iterator().next());
            }else {
                Notification.show("Please select users");
            }
        });

        buttonLayout.add(addButton, updateButton, deleteButton);
        add(buttonLayout);
    }

    public void gridIntit(){
        horizontalLayout = new HorizontalLayout();
        userGrid = new Grid<>();

        userGrid.setWidthFull();
        horizontalLayout.setWidthFull();

        Grid.Column<User> idColumn = userGrid.addColumn(User::getId);
        Grid.Column<User> nameColumn = userGrid.addColumn(User::getName);
        userGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(user -> presenter.getUserById(user.getId()));
        });

        userGrid.setItems(presenter.getAllUser());
        horizontalLayout.add(userGrid);
        add(horizontalLayout);
    }

    public void initFieldLayout(){
        formLayout = new FormLayout();
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        idField = new IntegerField("Id");
        nameField  = new TextField("Name");

        idField.setEnabled(false);
        formLayout.setEnabled(false);
        formLayout.setColspan(idField, 2);
        formLayout.setColspan(nameField, 2);

        saveButton.addClickListener(event -> {
           addUser();
        });
        cancelButton.addClickListener(event -> {
           formLayout.setEnabled(false);
        });

        formLayout.add(idField, nameField, saveButton, cancelButton);
        horizontalLayout.add(formLayout);
    }

    public void initBinder(){
        userBinder = new Binder<>();
        userBinder.setBean(new User());

        userBinder.forField(idField).bind(User::getId, User::setId);
        userBinder.forField(nameField)
                .withValidator(u -> u.length()>0, "Enter valid name")
                .bind(User::getName, User::setName);
    }

    public void addUser(){

        User bean = userBinder.getBean();
        userBinder.validate();
        if(userBinder.isValid()) {
            presenter.addUser(bean);
            formLayout.setEnabled(false);
            ListDataProvider dataProvider = (ListDataProvider) userGrid.getDataProvider();
            if(!dataProvider.getItems().contains(bean)){
                dataProvider.getItems().add(bean);
            }
            dataProvider.refreshAll();
        }
    }

    public void deleteUser(User user){

        ListDataProvider dataProvider = (ListDataProvider) userGrid.getDataProvider();
        dataProvider.getItems().remove(user);
        dataProvider.refreshAll();
        presenter.deleteUser(user);
    }

    public void sendAndReceiveMessage(){
        messageField = new TextField("Message Field");
        sendMEssageButton = new Button("Send");
        receiveButton = new Button("Receive");
        messageGrid = new Grid<>();
        ListDataProvider listDataProvider = (ListDataProvider) messageGrid.getDataProvider();

        sendMEssageButton.addClickListener(event -> {
            messageController.sendMessage(messageField.getValue());
        });

        receiveButton.addClickListener(event -> {
            String o = (String)jmsTemplate.receiveAndConvert("sample.queue");
            listDataProvider.getItems().add(o);
        });
        add(messageField, sendMEssageButton, receiveButton, messageGrid);

    }
}
