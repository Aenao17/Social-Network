package com.lab.laboratorsapte.ui;

import com.lab.laboratorsapte.domain.Friendship;
import com.lab.laboratorsapte.domain.Message;
import com.lab.laboratorsapte.domain.Tuple;
import com.lab.laboratorsapte.domain.User;
import com.lab.laboratorsapte.repository.DBFriendshipsRepo;
import com.lab.laboratorsapte.repository.DBUsersRepo;
import com.lab.laboratorsapte.service.FriendshipService;
import com.lab.laboratorsapte.service.MessageService;
import com.lab.laboratorsapte.service.Service;
import com.lab.laboratorsapte.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class UiImplementation implements UI{
    private UserService serviceu;
    private FriendshipService servicef;
    private MessageService messageService;

    public UiImplementation(UserService serviceu, FriendshipService servicef, MessageService servicem) {
        this.serviceu = serviceu;
        this.servicef = servicef;
        this.messageService = servicem;
    }

    @Override
    public void run() {
        menu();
        Scanner scanner = new Scanner(System.in);
        int i = scanner.nextInt();
        while(i != 0){
            option(i);
            menu();
            i = scanner.nextInt();
        }
        System.out.println("Exiting...");
    }
    private void menu(){
        System.out.println("Menu");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Show all users");
        System.out.println("6. Show all friendships");
        System.out.println("7. Show number of communities");
        System.out.println("8. Show most sociable community");
        System.out.println("9. Show all friendships for a user by month");
        System.out.println("10. Afiseaza toata conv intre doi useri");
        System.out.println(("0. Exit"));
    }
    private void option(int i){
        switch(i){
            case 1:
                //add user
                uiAddUser();
                break;
            case 2:
                //remove user
                uiRemoveUser();
                break;
            case 3:
                //add friendship
                uiAddFriendship();
                break;
            case 4:
                //remove friendship
                uiRemoveFriendship();
                break;
            case 5:
                //show all users
                uiShowAllUsers();
                break;
            case 6:
                //show all friendships
                uiShowAllFriendships();
                break;
            case 7:
                //show number of communities
                uiShowNumberOfCommunities();
                break;
            case 8:
                //show most sociable community
                uiShowMostSociableCommunity();
                break;
            case 9:
                //add predefined values
                uiNew();
                break;
            case 10:
                //cerinta noua
                uiMesaje();
                break;
            default:
                System.out.println("There is no option with this number!");
        }
    }

    private void uiMesaje(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first user's id: ");
        String Email1 = scanner.nextLine();
        System.out.println("Enter the second user's id: ");
        String Email2 = scanner.nextLine();
        ArrayList< Message > all = messageService.getMessagesBetweenTwoUsers(Long.valueOf(Email1), Long.valueOf(Email2));
        for(Message m : all){
            System.out.println(m);
        }
    }

    private void uiNew()
    {
        System.out.println("Stay tuined!");
    }
    private void uiAddUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Id: ");
        String Id = scanner.nextLine();
        System.out.println("Nume: ");
        String Nume = scanner.nextLine();
        System.out.println("Prenume: ");
        String Prenume = scanner.nextLine();
        System.out.println("Email: ");
        String Email = scanner.nextLine();
        try{
            serviceu.add(new User(Nume,Prenume,Email,Long.valueOf(Id),""));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void uiRemoveUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Id: ");
        String Id = scanner.nextLine();
        try{
            serviceu.delete(Long.valueOf(Id));
        }catch(Exception e){

        }
    }

    private void uiAddFriendship(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first user's id: ");
        String Email1 = scanner.nextLine();
        System.out.println("Enter the second user's id: ");
        String Email2 = scanner.nextLine();
        try{
            Friendship fr = new Friendship(Long.valueOf(Email1), Long.valueOf(Email2), LocalDateTime.now());
            servicef.add(fr);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void uiRemoveFriendship(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first user's id: ");
        String Email1 = scanner.nextLine();
        System.out.println("Enter the second user's id: ");
        String Email2 = scanner.nextLine();
        try{
            servicef.delete(new Tuple<Long, Long>(Long.valueOf(Email1),Long.valueOf(Email2)));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void uiShowAllUsers(){
        int cnt=0;
        for(Object user : serviceu.getAll()){
            System.out.println(user);
            cnt++;
        }
        System.out.println(cnt + " useri");
    }

    void uiShowAllFriendships(){
        int cnt=0;
        for(Object friends : servicef.getAll()){
            System.out.println(friends);
            cnt++;
        }
        if (cnt == 1)
            System.out.println(cnt + " prietenie exista ");
        else{
            System.out.println(cnt + " prietenii exista ");
        }
    }

    void uiShowNumberOfCommunities(){

    }

    void uiShowMostSociableCommunity(){

    }

}

