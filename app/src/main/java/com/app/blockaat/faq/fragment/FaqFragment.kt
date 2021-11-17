package com.app.blockaat.faq.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.faq.adapter.FaqAdapter
import com.app.blockaat.faq.interfaces.FaqInterface
import com.app.blockaat.faq.model.FaqDataModel
import com.app.blockaat.faq.model.FaqResponseModel
import com.app.blockaat.helper.CustomProgressBar
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.NetworkUtil
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_faq.view.*


class FaqFragment : Fragment() {
    private val arrListFaq: ArrayList<FaqDataModel> = arrayListOf()
    private var dialog: CustomProgressBar? = null
    private lateinit var mActvity: NavigationActivity
    private var rcyFaq: RecyclerView? = null
    private var isFromRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActvity = activity as NavigationActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        init(view)
        return view
    }

    private fun init(rootView: View) {
        dialog = CustomProgressBar(mActvity)
        rcyFaq = rootView.findViewById(R.id.rcyFaq)
        rootView.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            rootView.swipeRefreshLayout.isRefreshing = true
            rootView.swipeRefreshLayout.postDelayed(Runnable {
                rootView.swipeRefreshLayout.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(mActvity!!) != 0) {
                    getFaq()
                } else {
                    Global.showSnackbar(mActvity!!, resources.getString(R.string.connection_error))
                }
            }, 1000)
        })
        getFaq()
    }

    ////getting faq page data
    private fun getFaq() {
        if (NetworkUtil.getConnectivityStatus(mActvity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(mActvity)

            Global.apiService.getFaqData(
                com.app.blockaat.apimanager.WebServices.FaqWs + "lang=" + Global.getLanguage(
                    mActvity
                ) + "&store=" + Global.getStoreCode(
                    mActvity
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        } else {
            Global.showSnackbar(mActvity, resources.getString(R.string.connection_error))
        }
    }
    ////

    ///handling success response
    private fun handleResponse(model: FaqResponseModel?) {

        if (!isFromRefresh)
            hideProgressDialog()

        println("Success")

        if (model != null) {

            if (model.status == 200) {
                arrListFaq.clear()
              /*    arrListFaq.add(FaqDataModel(1, "Shipping and delivery", "Orders are processed between 1-2 business days after receipt of payment. For example, if an order is placed after 9am, we will aim to process and dispatch your order the following day. Once dispatched, delivery can be expected between 2-3 business days for major centers. For outlying areas, please allow a further 2-5 business days depending."))
                  arrListFaq.add(FaqDataModel(2, "Returns and refunds", "Orders are processed between 1-2 business days after receipt of payment. For example, if an order is placed after 9am, we will aim to process and dispatch your order the following day. Once dispatched, delivery can be expected between 2-3 business days for major centers. For outlying areas, please allow a further 2-5 business days depending."))
                  arrListFaq.add(FaqDataModel(3, "payment, pricing and promotions", "Orders are processed between 1-2 business "))*/
                model.data.let { arrListFaq.addAll(it) }
                rcyFaq?.visibility = View.VISIBLE
                val layoutManager = GridLayoutManager(mActvity, 1)
                val adapter = FaqAdapter(arrListFaq, mActvity/*, interfaceFaq*/)
                rcyFaq?.layoutManager = layoutManager
                rcyFaq?.adapter = adapter

            } else {
                rcyFaq?.visibility = View.GONE
            }
        }
    }
    ////

    private val interfaceFaq: FaqInterface = object : FaqInterface {
        override fun onItemClick(position: Int, type: String) {
            if (type == "openFaq") {
                mActvity.loadFaqDetailsFragment(arrListFaq[position])
            }
        }
    }

    ///handling error
    private fun handleError(error: Throwable) {

        if (!isFromRefresh)
            hideProgressDialog()

        println("Error : " + error.localizedMessage)
    }
    ////


    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }
}