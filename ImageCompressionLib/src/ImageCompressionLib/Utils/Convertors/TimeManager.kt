package ImageCompressionLib.Utils.Convertors

//import scala.collection.mutable.ListMap
import java.util.*

class TimeManager private constructor(){
    companion object {
        @JvmStatic val Instance=TimeManager()
    }
    var events:ArrayList<Event> = java.util.ArrayList()
    fun startNewTrack(name:String="My Task"){
        events= ArrayList()
        val t=Date().time
        events.add(Event(t,name))
    }
    fun append(endedTaskName:String){
        val t=Date().time
        events.add(Event(t,endedTaskName))
    }
    fun getInfoInPercent():String{
        val sb=StringBuilder()
        sb.append("${events[0].name}:")
        val totalTime=(events[events.size-1] - events[0])
        for(i in 1..events.size-1){
            sb.append(""""${events[i].name}"=""" +
                    """${((events[i]-events[i-1]).toDouble()/totalTime.toDouble()*100).toInt()}%; """)
        }
        return sb.toString()
    }
    fun getInfoInSec():String{
        val sb=StringBuilder()
        sb.append("${events[0].name}:")
        val totalTime=(events[events.size-1] - events[0])
        sb.append("Total:${totalTime}ms ")
        for(i in 1..events.size-1){
            if(events[i]-events[i-1]>0)
                sb.append(""""${events[i-1].name}"=""" +
                    """${(events[i]-events[i-1])}ms; """)
        }
        return sb.toString()
    }
    fun getTotalTime():Long{
        val totalTime=(events[events.size-1] - events[0])
        return totalTime
    }
    fun getLineSegment(index:Int):Long{
        if(index<0||index>=events.size)
            return -1

        return events[index+1]-events[index]
    }

    class Event(val time:Long,val name:String){
        operator fun minus(e:Event):Long{
            return this.time-e.time
        }
        operator fun plus(e:Event):Long{
            return this.time+e.time;
        }
        operator fun rem(totalTime:Long):Int{
            return (time/totalTime*100).toInt()
        }
    }

}