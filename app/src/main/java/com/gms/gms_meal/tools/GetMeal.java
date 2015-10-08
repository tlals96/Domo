package com.gms.gms_meal.tools;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.gms.gms_meal.Meal_Package.DinnerViewFragment;
import com.gms.gms_meal.Meal_Package.LunchViewFragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kam6376 on 2015-06-08.
 */
public class GetMeal extends AsyncTask<Integer, Void, String[]> {


  String where;
  int pos;

  public GetMeal(String from, int position) {
    where = from;
    pos = position;
  }

  @Override
  protected void onCancelled(String[] strings) {
    super.onCancelled(strings);
  }

  @Override
  protected void onPreExecute() {

    super.onPreExecute();

  }

  @SuppressWarnings("deprecation")
  @SuppressLint("SimpleDateFormat")
  @Override
  protected String[] doInBackground(Integer... AddDay) {
    // String date, day, lunchText, dinnerText;
    String[] info = new String[4];
    URL url = null; // URL��������
    HttpURLConnection urlConnection = null; // URL�����û ���� ����
    BufferedInputStream buf = null;// ���ۿ� �Է��� ����
    try {// �õ���


      Date now = new Date();

      SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
      now.setDate(now.getDate() + AddDay[0]);
      // String http =
      // "http://www.gms.hs.kr/lunch.view?date=20140901";
      String http = "http://www.gms.hs.kr/lunch.view?date=" + format.format(now);

      format = new SimpleDateFormat("yyyy/MM/dd");
      info[0] = format.format(now);
      // Date.setText(format.format(now));

      url = new URL(http);// URL������ ��û�� "���� ��  �޽�" URL
      urlConnection = (HttpURLConnection) url.openConnection();// URL��û

      buf = new BufferedInputStream(urlConnection.getInputStream());// ���ۿ�
      // URL����


      BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr")); // �ѱ� ���ڵ����� ���۸�
      // ����

      String line = null; // ���ڿ����� line ���� ��, null�� ���.
      String Lunch_page = ""; // null��� line�� ������� �� ���̹Ƿ�, ����ó��.
      String Dinner_page = "";

      int Week;
      Calendar Cal_Info = Calendar.getInstance();
      Week = Cal_Info.get(Calendar.DAY_OF_WEEK) + AddDay[0];


      if (6 < Week)
        Week %= 7;

      switch (Week) {
        case 0:
          // Day.setText("�����");
          info[1] = "�����";
          break;
        case 1:
          // Day.setText("�Ͽ���");
          info[1] = "�Ͽ���";
          break;
        case 2:
          // Day.setText("������");
          info[1] = "������";
          break;
        case 3:
          // Day.setText("ȭ����");
          info[1] = "ȭ����";
          break;
        case 4:
          // Day.setText("������");
          info[1] = "������";
          break;
        case 5:
          // Day.setText("�����");
          info[1] = "�����";
          break;
        case 6:
          // Day.setText("�ݿ���");
          info[1] = "�ݿ���";
          break;

      }

      switch (Week) {
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
          while ((line = bufreader.readLine()) != null) { // ��û�� ULR
            // �ҽ�(html)������ �������
            // ���� ���.
            Lunch_page += line; // "page" ���ڿ� ������ "line" ���ڿ� ������
            // �������
            // �Է�.
            Dinner_page += line;
          }
          info[2] = Get_Lunch(Lunch_page);
          info[3] = Get_Dinner(Dinner_page);
          break;

        case 0:
        case 1:

          // Lunch_Text.setText("������ �޽��� ������ ����� �����ϴ�.");
          info[2] = "���������� �����ϴ�.";
          // Dinner_Text.setText("������ �޽��� ������ ����� �����ϴ�.");
          info[3] = "���������� �����ϴ�.";
          break;
      }

    } catch (Exception e) {// �����߻��� ����.
//      Log.e("Meal", "succes " + e);
      // Lunch_Text.setText("������ ������ Ȯ���� �ֽʽÿ�.");// �����̺�Ʈ�� �Բ� ������ �޼���
      // ���.
      info[2] = "������ ������ Ȯ���� �ֽʽÿ�.";
      // Dinner_Text.setText("������ ������ Ȯ���� �ֽʽÿ�.");
      info[3] = "������ ������ Ȯ���� �ֽʽÿ�.";

    } finally {
      urlConnection.disconnect();
    }

    for (int i = 0; i < info.length; i++) {
//      Log.e("get", "//" + info[i]);
    }
    return info;
  }

  protected void onPostExecute(String[] Info) {


    if (where == "lunch") {
      Message msg = LunchViewFragment.getMealHandler.obtainMessage();
      Bundle data = new Bundle();
      data.putStringArray("info", Info);
      data.putInt("pos", pos);
      msg.setData(data);
      LunchViewFragment.getMealHandler.sendMessage(msg);
    } else if (where == "dinner") {
      Message msg = DinnerViewFragment.getMealHandler.obtainMessage();
      Bundle data = new Bundle();
      data.putStringArray("info", Info);
      data.putInt("pos", pos);
      msg.setData(data);
      DinnerViewFragment.getMealHandler.sendMessage(msg);
    }

  }


  private String Get_Lunch(String Lunch_page_Alpha) {

    try {

      int Lunch_Start, Lunch_End;

      Lunch_Start = Lunch_page_Alpha.indexOf("morning");
      Lunch_End = Lunch_page_Alpha.indexOf("calorie");

      Lunch_page_Alpha = Lunch_page_Alpha.substring(Lunch_Start, Lunch_End);

      Lunch_page_Alpha = Lunch_page_Alpha.replaceAll("[^��-�� <br <]", "");
      Lunch_page_Alpha = Lunch_page_Alpha.replaceAll("[<br]", " ");
      Lunch_page_Alpha = Lunch_page_Alpha.replaceAll("�߽�", "");

      Lunch_page_Alpha = Lunch_page_Alpha.trim();

//      Log.e("text", Lunch_page_Alpha);

      // Lunch_Text.setText(Lunch_page_Alpha);

    } catch (Exception e) {
//      Log.e("Meal", "succes " + e);
      Lunch_page_Alpha = "�޽������� �����ϴ�.";
      // Lunch_Text.setText("�޽������� �����ϴ�.");// �����̺�Ʈ�� �Բ� ������ �޼��� ���.
    }
    return Lunch_page_Alpha;

  }

  private String Get_Dinner(String Dinner_page_Alpha) {
    try {
      int Dinner_Start, Dinner_End;
      // Lunch_Start = Lunch_page.indexOf("menuName");

      Dinner_Start = Dinner_page_Alpha.indexOf("����");
      Dinner_End = Dinner_page_Alpha.indexOf("<!--�߾� ������ ���� ��-->");
//      Log.e("DS", "" + Dinner_Start);
//      Log.e("DE", "" + Dinner_End);

      Dinner_page_Alpha = Dinner_page_Alpha.substring(Dinner_Start, Dinner_End);
      Dinner_End = Dinner_page_Alpha.indexOf("������");
      Dinner_page_Alpha = Dinner_page_Alpha.substring(0, Dinner_End);

      Dinner_page_Alpha = Dinner_page_Alpha.replaceAll("[^��-�� <br <]", "");
      Dinner_page_Alpha = Dinner_page_Alpha.replaceAll("[<br]", "\n");
      Dinner_page_Alpha = Dinner_page_Alpha.replaceAll("����", "");
      Dinner_page_Alpha = Dinner_page_Alpha.replaceAll("\n", " ");


      Dinner_page_Alpha = Dinner_page_Alpha.trim();

//      Log.e("text", Dinner_page_Alpha);

      // Dinner_Text.setText(Dinner_page_Alpha);

    } catch (Exception e) {
      Dinner_page_Alpha = "�޽������� �����ϴ�.";
      // Dinner_Text.setText("�޽������� �����ϴ�.");// �����̺�Ʈ�� �Բ� ������ �޼��� ���.
    }
    return Dinner_page_Alpha;
  }
}