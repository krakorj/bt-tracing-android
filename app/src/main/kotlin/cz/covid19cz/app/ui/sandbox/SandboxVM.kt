package cz.covid19cz.app.ui.sandbox

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import arch.livedata.SafeMutableLiveData
import com.google.firebase.auth.FirebaseAuth
import cz.covid19cz.app.AppConfig
import cz.covid19cz.app.R
import cz.covid19cz.app.bt.BluetoothRepository
import cz.covid19cz.app.db.DatabaseRepository
import cz.covid19cz.app.db.SharedPrefsRepository
import cz.covid19cz.app.db.export.CsvExporter
import cz.covid19cz.app.ui.base.BaseVM
import cz.covid19cz.app.ui.dashboard.event.DashboardCommandEvent
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class SandboxVM(
    val bluetoothRepository: BluetoothRepository,
    private val prefs : SharedPrefsRepository,
    private val repository: DatabaseRepository
) :
    BaseVM() {

    val buid = prefs.getDeviceBuid()
    val devices = bluetoothRepository.scanResultsList
    val serviceRunning = SafeMutableLiveData(false)
    val power = SafeMutableLiveData(0)
    val advertisingSupportText = MutableLiveData<String>().apply {
        value = if (bluetoothRepository.supportsAdvertising()){
            "Podporuje vysílání"
        } else {
            "Nepodporuje vysílání"
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        power.observeForever { value ->
            if (value == 0){
                // 0 means remote config
                AppConfig.overrideAdvertiseTxPower = null
            } else {
                // save overriding power setting (value-1)
                AppConfig.overrideAdvertiseTxPower = value-1
            }
        }
    }



    fun start() {
        publish(DashboardCommandEvent(DashboardCommandEvent.Command.TURN_ON))
    }

    fun confirmStart() {
        serviceRunning.value = true
    }

    fun stop() {
        serviceRunning.value = false
        publish(DashboardCommandEvent(DashboardCommandEvent.Command.TURN_OFF))
    }


    fun openDbExplorer(){
        navigate(R.id.action_nav_sandbox_to_nav_my_data)
    }

    fun nuke() {
        prefs.clear()
        Completable.fromAction(repository::clear)
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navigate(R.id.action_nav_sandbox_to_nav_welcome_fragment)
            }
        FirebaseAuth.getInstance().signOut()
    }



}