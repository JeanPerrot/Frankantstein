import com.google.gson.Gson
import subsume.fight.QLearning
import subsume.fight.CondensedState
import ants.Aim
import util.Rotate

def file = new File("/Users/jperrot/github/fun/ants/ants/antLog.log")
def gson = new Gson()

def bigMap = [:]

def count=0
def found=0;
file.eachLine {line ->
    if (!line.startsWith("INFO: {")) {
        return
    }
    count++
    if (count%10000==0){
        println "processing line $count"
    }
    def json = line.substring(5)
    def parsed = gson.fromJson(json, QLearning.LearningTuple.class)
    if (new String(parsed.state.asChars()).equals("+++/////")){
        found++
        println line
    }else{
        return
    }
    
    def choices = bigMap.get(parsed)
    if (choices == null) {
        choices = [:]
        bigMap[parsed.state] = choices
    }

    def values = choices[parsed.decision]
    if (values == null) {
        values = []
        choices[parsed.decision] = values
    }
    values << parsed.reward
}

println "processed $count lines"
println "number of states: ${bigMap.size()}"
println "number of found: $found"

println "average number of hits per state is ${count/bigMap.size()}"
boolean[] land=new boolean [4]
Arrays.fill(land,true)
def win=bigMap.get(new CondensedState("+++/////".toCharArray(),land,Aim.NORTH));
println win

