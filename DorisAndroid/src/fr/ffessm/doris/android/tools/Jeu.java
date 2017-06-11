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
package fr.ffessm.doris.android.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.fragments.JeuxReponses_ClassListViewFragment;

public class Jeu {
	private static final String LOG_TAG = Jeu.class.getCanonicalName();

    public enum Statut { ACCUEIL, CHOIX_NIVEAU, JEU }
    public enum JeuRef { JEU_1, JEU_2 }
    public enum Niveau { FACILE, INTERMEDIAIRE, DIFFICILE }
    public int NBREPONSESPROPOSEES = 3;

    private Context context;
    private Activity activity;

    private JeuRef jeuRef;
    private Niveau niveau;

	public Jeu(Context context){
		this.context = context;
	}
	public Jeu(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
	}
    public Jeu(Context context, Activity activity, JeuRef jeuRef) {
        this.context = context;
        this.activity = activity;
        this.jeuRef = jeuRef;
    }

	public Jeu(JeuRef id){
        this.jeuRef = id;
	}

    public JeuRef getId() {return jeuRef;}
    public void setId(JeuRef jeu_id){
        this.jeuRef = jeu_id;
    }

    public Niveau getNiveau() {return niveau;}
	public void setNiveau(Niveau niveau){
        this.niveau = niveau;
    }

    public static View getJeuView(final JeuxReponses_ClassListViewFragment.JeuSelectionneListener jeuSelectionneCallback, final Context context, final JeuRef jeuRef){
        Log.d(LOG_TAG, "getJeuView() - Début");

        String jeux_libelle[] = context.getResources().getStringArray(R.array.jeux_titre_array);

        String jeux_icone[] = context.getResources().getStringArray(R.array.jeux_titre_icone);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewReponse = inflater.inflate(R.layout.jeux_reponseview_row, null);

        TextView tvLabel = (TextView) viewReponse.findViewById(R.id.jeux_listviewrow_label);
        tvLabel.setText(jeux_libelle[jeuRef.ordinal()]);

        ImageView ivIcone = (ImageView) viewReponse.findViewById(R.id.jeux_listviewrow_icon);
        ivIcone.setImageResource(context.getResources().getIdentifier(jeux_icone[jeuRef.ordinal()],"drawable", context.getPackageName()));

        ImageView ivReponseInfoIcone = (ImageView) viewReponse.findViewById(R.id.jeux_listviewrow_reponseinfo_icon);
        ivReponseInfoIcone.setVisibility(View.GONE);

        viewReponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jeuSelectionneCallback != null)
                    jeuSelectionneCallback.onJeuSelectionne(jeuRef);
            }
        });

        Log.d(LOG_TAG, "getJeuView() - Fin");
        return viewReponse;
    }

    public static View getNiveauView(final JeuxReponses_ClassListViewFragment.NiveauSelectionneListener niveauSelectionneCallback, final Context context, final JeuRef jeuRef, final Niveau niveau){
        Log.d(LOG_TAG, "getNiveauView() - Début");

        String niveau_libelle[] = context.getResources().getStringArray(R.array.jeux_niveau_array);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewReponse = inflater.inflate(R.layout.jeux_reponseview_row, null);

        TextView tvLabel = (TextView) viewReponse.findViewById(R.id.jeux_listviewrow_label);
        tvLabel.setText(niveau_libelle[niveau.ordinal()]);

        ImageView ivReponseInfoIcone = (ImageView) viewReponse.findViewById(R.id.jeux_listviewrow_reponseinfo_icon);
        ivReponseInfoIcone.setVisibility(View.GONE);

        viewReponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "getNiveauView() - onClick()");

                if(niveauSelectionneCallback != null)
                    niveauSelectionneCallback.onNiveauSelectionne(jeuRef, niveau, false);
            }
        });

        Log.d(LOG_TAG, "getNiveauView() - Fin");
        return viewReponse;
    }

    public View getReponseView(final JeuxReponses_ClassListViewFragment.ReponseSelectionneeListener reponseSelectionneeCallback, final Fiche ficheQuestion, final int idReponse,
                               final String labelReponse, final String iconeReponse, final String DetailReponse){
        Log.d(LOG_TAG, "getReponseView() - Début");

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewReponse = inflater.inflate(R.layout.jeux_reponseview_row, null);

        TextView tvLabel = (TextView) viewReponse.findViewById(R.id.jeux_listviewrow_label);
        tvLabel.setText(labelReponse);

        ImageView ivIcone = (ImageView) viewReponse.findViewById(R.id.jeux_listviewrow_icon);
        ivIcone.setImageResource(R.drawable.app_ic_launcher);

        final ImageView ivIconeFinal = ivIcone;

        LinearLayout llReponse = (LinearLayout) viewReponse.findViewById(R.id.jeux_listviewrow_click);
        llReponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "getReponseView() - onClick()");

                if(reponseSelectionneeCallback != null)
                    reponseSelectionneeCallback.onReponseSelectionnee(ficheQuestion, idReponse, ivIconeFinal);
            }
        });

        ImageView ivReponseInfoIcon = (ImageView) viewReponse.findViewById(R.id.jeux_listviewrow_reponseinfo_icon);
        ivReponseInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "getReponseView() - detail.onClick()");

                String descriptionReponse = DetailReponse;
                if (DetailReponse == null || DetailReponse == "") descriptionReponse = "Pas de détails pour cette réponse";

                TextView tvDetails = (TextView) viewReponse.findViewById(R.id.jeux_listviewrow_details);
                tvDetails.setText(descriptionReponse);
            }
        });

        Log.d(LOG_TAG, "getReponseView() - Fin");
        return viewReponse;
    }

    public static int[] getBornesClassification(JeuRef id, Niveau niveau){
        int borne[] = {0,99};

        if (niveau == Niveau.FACILE){
            borne[0] = 0;
            borne[1] = 3;
        }
        if (niveau == Niveau.INTERMEDIAIRE){
            borne[0] = 2;
            borne[1] = 7;
        }
        if (niveau == Niveau.DIFFICILE){
            borne[0] = 3;
            borne[1] = 20;
        }

        return borne;
    }

}
