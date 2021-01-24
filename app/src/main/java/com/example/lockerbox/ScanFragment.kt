package com.example.lockerbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_response_locker.*


class ScanFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                if(it.text.subSequence(0,3).toString() == "LOK")
                {
//                    Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                    val action = ScanFragmentDirections.actionScanFragmentToResponseLockerFragment(it.text)
                    Navigation.findNavController(view).navigate(action)
//                    findNavController().navigate(R.id.action_scanQRFragment_to_choseBoxFragment)
                }
                else
                {
                    Toast.makeText(activity, "Kode QR SALAH", Toast.LENGTH_LONG).show()
                     findNavController().navigate((R.id.action_scanFragment_to_homeFragment))
                }
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


}