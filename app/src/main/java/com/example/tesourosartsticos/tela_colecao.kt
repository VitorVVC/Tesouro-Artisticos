import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tesourosartsticos.R
import com.example.tesourosartsticos.models.Obra
import com.example.tesourosartsticos.models.ObrasAdapter
import com.example.tesourosartsticos.models.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "TelaColecaoFragment"

class tela_colecao : Fragment() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userViewModel.userPath = it.getString("USER_PATH")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tela_colecao, container, false)

        // Consulta as obras específicas da coleção do usuário
        userViewModel.userPath?.let { userPath ->
            val db = Firebase.firestore
            db.collection("Logins/$userPath/ObrasUser")
                .get()
                .addOnSuccessListener { result ->
                    val obrasList = mutableListOf<Obra>()
                    for (document in result) {
                        val nome = document.getString("titulo")
                        val imageUrl = document.getString("imageUrl")
                        val autor = document.getString("autor")
                        val descricao = document.getString("descricao")
                        if (nome != null && imageUrl != null) {
                            val obra = Obra(nome, imageUrl, autor, descricao)
                            obrasList.add(obra)
                        }
                    }
                    setupRecyclerView(view, obrasList)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        }

        return view
    }

    private fun setupRecyclerView(view: View, obrasList: List<Obra>) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleItems)
        val adapter = ObrasAdapter(obrasList)
        adapter.setUserPath(userViewModel.userPath)
        recyclerView.adapter = adapter
    }


    companion object {
        @JvmStatic
        fun newInstance(userP: String) =
            tela_colecao().apply {
                arguments = Bundle().apply {
                    putString("USER_PATH", userP)
                }
            }
    }
}
