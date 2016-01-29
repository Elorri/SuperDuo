/**
 * The MIT License (MIT)

 Copyright (c) 2015 ETCHEMENDY ELORRI

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package it.jaschke.alexandria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.extras.Tools;
/**
 * This activity is the launcher. The only thing it does is chosing what screen will be displayed
 * to the user.
 * Created by Elorri on 19/01/2016.
 */
public class MainActivity extends AppCompatActivity {

   public static boolean isAddFirstScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddFirstScreen = (Tools.getMainPagePreferences(this))
                .equals(getResources().getString(R.string.pref_start_page_add));

        finish();
        if (isAddFirstScreen) {
            startActivity(new Intent(this, AddActivity.class));
        } else {
            startActivity(new Intent(this, ListActivity.class));
        }

    }


}