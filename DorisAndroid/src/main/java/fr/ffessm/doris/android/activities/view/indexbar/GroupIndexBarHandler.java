/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.tools.Groupes_Outils;

/**
 * Handler fro Groupe Index bar
 */
public class GroupIndexBarHandler extends Handler {

    static String TAG = "IndexBarHandler";
    ActivityWithIndexBar indexbar;
    ListView notifiedListview;
    int filtreGroupe = 1;
    int height_of_listview = 0;
    int height_of_row = 0;
    int number_of_groups_shown = 0;

    /**
     * Constructor of IndexBarHandler class which reserves the instance reference of its UI class
     *
     * @param indexbar
     */
    public GroupIndexBarHandler(ActivityWithIndexBar indexbar, ListView notifiedListview, int filtreGroupe) {
        this.indexbar = indexbar;
        this.notifiedListview = notifiedListview;
        this.filtreGroupe = filtreGroupe;
    }

    public void handleMessage(Message msg) {
        if (msg.what == IndxBarHandlerMessages.ON_LAYOUT_EVT) { // msg = 1 layout changed
            //Log.d(TAG,"IndexBarHandler called. Custom Listview is created. Now initializing Alphabet IndexBar Scroller Listview.");
            /*Before creating the Index Bar Listview we need to determine the number of alphabets
             * to be shown in this listview.
             */

            height_of_listview = (Integer) msg.obj;            //Height of the ListView in pixels

            //View view=indexbar.findViewById(R.id.alphabet_row_layout);
            View view = indexbar.getAlphabetRowView();

            height_of_row = view.getHeight();            //Height of the row.
            indexbar.getSharedPreferences("AndroidIndexBar", Context.MODE_PRIVATE).edit().putInt("height", height_of_row).apply();
            view.setVisibility(View.GONE);              // this view is here only to help compute the size, it will be replaced by concrete ones in the correct location

            List<Groupe> groups_list = GroupeListProvider.getFilteredGroupeList(indexbar.getDorisDBHelper(), filtreGroupe);
            //Log.d(TAG,"Height of List= "+height_of_listview+"    height of one row="+height_of_alphabet_row);
            number_of_groups_shown = (height_of_listview / (height_of_row ));                //Number of Characters to be shown
            //Log.d(TAG,"Number of Characters="+number_of_characters_shown+"  omits="+number_of_characters_omit);


            //Alphabets listview is populated
            // TODO trouver un moyen indenpendant de la vue

            //ListView groups_listview=(ListView)indexbar.findViewById(R.id.listeficheavecfiltre_listView_alphabets);
            ListView groups_listview = indexbar.getAlphabetListView();
            /*Populating the Alphabet List*/

            /*Main Task*/
            prepareArray(groups_list);

            ArrayAdapter<Groupe> groups_adapter = new GroupListviewAdapter(indexbar.getBaseContext(), R.layout.indexbar_image_row, groups_list);
            groups_listview.setAdapter(groups_adapter);
            groups_listview.setOnItemClickListener((OnItemClickListener) indexbar);
        }
        // msg = 2 update required of the content
        if (msg.what == IndxBarHandlerMessages.ON_RESUME_GROUP_EVT && height_of_listview != 0) {

            filtreGroupe = (Integer) msg.obj;            //update of the filtreGroupe
            List<Groupe> groups_list = GroupeListProvider.getFilteredGroupeList(indexbar.getDorisDBHelper(), filtreGroupe);
            //Log.d(TAG,"Height of List= "+height_of_listview+"    height of one row="+height_of_alphabet_row);
            number_of_groups_shown = height_of_listview / height_of_row;                //Number of Characters to be shown
            //Log.d(TAG,"Number of Characters="+number_of_characters_shown+"  omits="+number_of_characters_omit);


            //Alphabets listview is populated
            // TODO trouver un moyen indenpendant de la vue

            //ListView groups_listview=(ListView)indexbar.findViewById(R.id.listeficheavecfiltre_listView_alphabets);
            ListView groups_listview = indexbar.getAlphabetListView();

            prepareArray(groups_list);

            GroupListviewAdapter adapter = (GroupListviewAdapter) groups_listview.getAdapter();
            if (!Groupes_Outils.areEquivalentGroupLists(adapter.data, groups_list)) {
                adapter.data.clear();
                adapter.data.addAll(groups_list);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * removes as many group as required to fit the screen
     *
     * @param groups_list
     */
    private void prepareArray(List<Groupe> groups_list) {
        // images are 16dp height
        //Log.d(TAG,"Height of one dot i.e. 9dp= "+height_of_one_dot+" px");  //9dp=13px

        ArrayList<Groupe> groupeSelection = new ArrayList<>();

        // keep only a subset of the groups
        int size = groups_list.size();
        if (number_of_groups_shown >= size) {
            groupeSelection.addAll(groups_list);
        } else {
            for (int i = 1; i < size; i++) {
                if (i == 1 || i % (size / (number_of_groups_shown - 1 )) == 0) {
                    groupeSelection.add(groups_list.get(i));
                }
            }
        }

        groups_list.clear();
        groups_list.addAll(groupeSelection);


    }

    public int convertDipToPx(int dp, Context context) {    //10dp=15px
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }


}
