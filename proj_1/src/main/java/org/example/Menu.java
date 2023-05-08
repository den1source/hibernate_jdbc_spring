package org.example;

import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Menu {
    private boolean is_exit = false, it_is_end=true;

    public void menu() throws InterruptedException {
        print_munu();
        do {
            System.out.print("");
            if(it_is_end){
                input();
            }
        } while (!is_exit);
    }

    private void print_munu() {
        System.out.print("===================МЕНЮ===================\n");
        System.out.print("0) Выход\n" +
                "1) Вывести список\n" +
                "2) Удалить строчку\n" +
                "3) Добавить запись\n" +
                "4) Редактировать запись\n" +
                "5) Меню\n");
    }

    private void input() throws InterruptedException {
        System.out.print("Номер действия: ");
        Scanner keyboard = new Scanner(System.in);
        String vvod = keyboard.nextLine();
        if (isNumeric(vvod) && !vvod.contains(" ") && Integer.parseInt(vvod)>=0) {
            choose(Integer.parseInt(vvod));
        } else System.out.println("Ошибка, введите число");
    }



    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void choose(int num) throws InterruptedException {
        System.out.println("================РЕЗУЛЬТАТ=================");
        switch (num) {
            case 0 -> {
                is_exit = true;
                System.out.println("Всего доброго)");
                System.exit(0);
            }
            case 1 -> {
                out_db();
            }
            case 2 -> {
                boolean err = false;
                Scanner keyboard = new Scanner(System.in);
                System.out.print("Введите ID: ");
                String vvod = keyboard.nextLine();
                do {
                    if (isNumeric(vvod) && !vvod.contains(" ")) {
                        err = false;
                    } else {
                        err = true;
                        System.out.println("Ошибка! Введите номер класса еще раз");
                    }
                } while (err);
                delete_str(Integer.parseInt(vvod));
            }
            case 3 -> {
                boolean error = false;
                Scanner keyboard = new Scanner(System.in);
                System.out.print("Введите фамилию: ");
                String l_name = keyboard.nextLine();
                System.out.print("Введите имя: ");
                String name = keyboard.nextLine();
                String year = "";
                do {
                    System.out.print("Введите класс: ");
                    year = keyboard.nextLine();
                    if (!isNumeric(year) || year.contains(" ") || Integer.parseInt(year) > 11 || Integer.parseInt(year) < 0) {
                        error = true;
                        System.out.println("Ошибка! Введите номер класса еще раз");
                    } else error = false;
                } while (error);
                add_str(l_name, name, Integer.parseInt(year));
            }
            case 4 -> {
                boolean error = false;
                Scanner keyboard = new Scanner(System.in);
                String id = "";
                do {
                    System.out.print("Введите ID: ");
                    id = keyboard.nextLine();
                    if (!isNumeric(id) || id.contains(" ") || Integer.parseInt(id) < 0) {
                        error = true;
                        System.out.println("Ошибка! Введите ID еще раз");
                    } else error = false;
                } while (error);
                System.out.print("Введите фамилию: ");
                String l_name = keyboard.nextLine();
                System.out.print("Введите имя: ");
                String name = keyboard.nextLine();
                String year = "";
                do {
                    System.out.print("Введите класс: ");
                    year = keyboard.nextLine();
                    if (!isNumeric(year) || year.contains(" ") || Integer.parseInt(year) > 11 || Integer.parseInt(year) < 0) {
                        error = true;
                        System.out.println("Ошибка! Введите номер класса еще раз");
                    } else error = false;
                } while (error);
                edit_str(id, l_name, name, Integer.parseInt(year));
            }
            case 5 -> {
                print_munu();
            }
            default -> {
                System.out.println("Нет такого действия");
            }
        }

    }


    public void out_db() {
        it_is_end=false;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://127.0.1.1:8080/").newBuilder();
        urlBuilder.addQueryParameter("what_do", "out_db");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    ResponseBody responseData = response.body();
                    String responseString = responseData.string();
                    String[] responseList = (responseString.split(" ; "));
                    System.out.printf("%-4S %-15S %-15S %-5S\n", "ID", "Фамилия", "Имя", "Класс");
                    System.out.println("==========================================");
                    for (int i = 0; i < responseList.length; i++) {
                        String str = (Arrays.toString(responseList[i].split("   ")));
                        String id = str.substring(1, str.indexOf("LN"));
                        String ln = str.substring(str.indexOf("LN") + 2, str.indexOf("NM") - 2);
                        String nm = str.substring(str.indexOf("NM") + 2, str.indexOf("Y_S") - 2);
                        String num_y = str.substring(str.indexOf("Y_S") + 3, str.length() - 1);
                        System.out.printf("%-4S %-15S %-15S %5S\n", id, ln, nm, num_y);
                    }
                    System.out.println("==========================================");
                }
                it_is_end=true;

            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

    }



    public void delete_str(int id_str) {
        it_is_end=false;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://127.0.1.1:8080/").newBuilder();
        urlBuilder.addQueryParameter("what_do", "delete_str");
        urlBuilder.addQueryParameter("num_str_for_delete", String.valueOf(id_str));
        post_res(client, urlBuilder);
    }

    public void add_str(String lastname, String name, int num_y) {
        it_is_end=false;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://127.0.1.1:8080/").newBuilder();
        urlBuilder.addQueryParameter("what_do", "add_str");
        urlBuilder.addQueryParameter("lastname", lastname);
        urlBuilder.addQueryParameter("name", name);
        urlBuilder.addQueryParameter("num_y", String.valueOf(num_y));
        post_res(client, urlBuilder);
    }

    public void edit_str(String id, String lastname, String name, int num_y){
        it_is_end=false;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://127.0.1.1:8080/").newBuilder();
        urlBuilder.addQueryParameter("what_do", "edit_str");
        urlBuilder.addQueryParameter("id", id);
        urlBuilder.addQueryParameter("lastname", lastname);
        urlBuilder.addQueryParameter("name", name);
        urlBuilder.addQueryParameter("num_y", String.valueOf(num_y));
        post_res(client, urlBuilder);
    }

    private void post_res(OkHttpClient client, HttpUrl.Builder urlBuilder) {
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    ResponseBody responseData = response.body();
                    String responseString = responseData.string();
                    System.out.println("Ответ:" + responseString);
                }
                it_is_end=true;
            }


            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
