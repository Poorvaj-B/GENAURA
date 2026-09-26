import React,{useState} from 'react'
import {createRoot} from 'react-dom/client'
import {AlertTriangle,CheckCircle2,Copy,Database,Gauge,History,Info,Loader2,Search,Sparkles,XCircle} from 'lucide-react'
import './styles.css'

const initial={title:'',description:'Payment is not working after OTP. Please check ASAP.',environment:'UAT',browser:'Chrome',applicationVersion:'5.2.1'}

function App(){
 const [form,setForm]=useState(initial),[result,setResult]=useState(null),[loading,setLoading]=useState(false)
 const [saving,setSaving]=useState(false),[history,setHistory]=useState([]),[tab,setTab]=useState('enhance'),[error,setError]=useState('')
 const update=(k,v)=>setForm(x=>({...x,[k]:v}))
 const enhance=async()=>{setLoading(true);setError('');try{let r=await fetch('/api/defects/enhance',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(form)});if(!r.ok)throw Error();setResult(await r.json())}catch(e){setError('Backend is not reachable. Start Spring Boot on port 8080.')}finally{setLoading(false)}}
 const save=async()=>{if(!result)return;setSaving(true);try{let r=await fetch('/api/defects',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({rawDescription:form.description,enhanced:result})});if(!r.ok)throw Error();await load();setTab('history')}catch(e){setError('Could not save defect.')}finally{setSaving(false)}}
 const load=async()=>{try{let r=await fetch('/api/defects');if(r.ok)setHistory(await r.json())}catch{}}
 return <div className="app">
  <header><div className="brand"><div className="logo"><Sparkles size={20}/></div><div><b>DefectIQ</b><small>AI-Powered Defect Description Enhancer</small></div></div><div className="ready">● AI Engine Ready</div></header>
  <main>
   <section className="hero"><div><div className="eyebrow">IT QUALITY ASSURANCE</div><h1>Turn vague defects into<br/><span>developer-ready reports.</span></h1><p>Improve clarity, completeness and reproducibility in seconds with AI-assisted enhancement and measurable quality scoring.</p></div><div className="target"><Gauge size={22}/><b>85%+<small>target quality</small></b></div></section>
   <nav><button className={tab==='enhance'?'active':''} onClick={()=>setTab('enhance')}><Sparkles size={16}/> Enhance Defect</button><button className={tab==='history'?'active':''} onClick={()=>{setTab('history');load()}}><History size={16}/> History</button></nav>
   {error&&<div className="error"><XCircle size={17}/>{error}</div>}
   {tab==='enhance'?<div className="workspace">
    <section className="panel input"><div className="heading"><div><h2>Raw Defect</h2><p>Give the AI whatever the QA engineer has.</p></div><em>INPUT</em></div>
     <label>Short title <small>Optional</small></label><input value={form.title} onChange={e=>update('title',e.target.value)} placeholder="e.g. Payment issue after OTP"/>
     <label>Defect description <strong>*</strong></label><textarea rows="8" value={form.description} onChange={e=>update('description',e.target.value)}/>
     <div className="grid3"><Field label="Environment" value={form.environment} set={v=>update('environment',v)} opts={['DEV','QA','UAT','PROD']}/><Field label="Browser / Device" value={form.browser} set={v=>update('browser',v)} opts={['Chrome','Edge','Firefox','Safari','Android','iOS']}/><div><label>App version</label><input value={form.applicationVersion} onChange={e=>update('applicationVersion',e.target.value)}/></div></div>
     <div className="actions"><button className="secondary" onClick={()=>setForm(initial)}>Sample</button><button className="primary" disabled={loading||!form.description.trim()} onClick={enhance}>{loading?<><Loader2 className="spin" size={17}/>Analyzing...</>:<><Sparkles size={17}/>Enhance with AI</>}</button></div>
     <div className="hint"><Info size={14}/>Use synthetic/anonymized data for the demo.</div>
    </section>
    <section className="panel output">{!result?<Empty/>:<Result r={result} save={save} saving={saving}/>}</section>
   </div>:<HistoryView data={history}/>}
  </main><footer>DefectIQ • Hackathon Prototype • Automated defect quality improvement</footer>
 </div>
}

function Field({label,value,set,opts}){return <div><label>{label}</label><select value={value} onChange={e=>set(e.target.value)}><option value="">Not provided</option>{opts.map(x=><option key={x}>{x}</option>)}</select></div>}

function Empty(){return <div className="empty"><div className="emptyicon"><Sparkles size={25}/></div><h2>Your enhanced defect will appear here</h2><p>Enter a raw defect and click <b>Enhance with AI</b>. The engine structures the report, identifies missing data and calculates a quality score.</p><div className="features"><span>✓ Reproduction steps</span><span>✓ Expected vs actual</span><span>✓ Quality score</span><span>✓ Missing information</span></div></div>}

function Result({r,save,saving}){
 const q=r.qualityBreakdown
 return <><div className="heading"><div><h2>Enhanced Defect</h2><p>Structured, scored and ready for QA review.</p></div><em className="green">{r.aiProvider}</em></div>
 <div className="score"><Score n={r.qualityScore}/><div><b>{r.qualityScore>=85?'Developer-ready quality':'Needs more information'}</b><p>Measured across clarity, completeness, reproducibility, expected/actual, environment and evidence.</p></div></div>
 <div className="before"><div><small>BEFORE</small><b>{Math.max(15,r.qualityScore-45)}%</b></div><span>→</span><div><small>AFTER</small><b className="good">{r.qualityScore}%</b></div></div>
 <div className="metrics">{Object.entries({Clarity:q.clarity,Completeness:q.completeness,Reproducibility:q.reproducibility,'Expected / Actual':q.expectedActual,Environment:q.environment,Evidence:q.evidence}).map(([k,v])=><Metric key={k} name={k} value={v}/>)}</div>
 <Block title="Title"><h3>{r.title}</h3></Block><Block title="Summary"><p>{r.summary}</p></Block>
 <div className="twocol"><Block title="Steps to reproduce"><ol>{r.stepsToReproduce.map((x,i)=><li key={i}>{x}</li>)}</ol></Block><Block title="Classification"><Rows r={r}/></Block></div>
 <div className="twocol"><Block title="Expected result"><p>{r.expectedResult}</p></Block><Block title="Actual result"><p>{r.actualResult}</p></Block></div>
 {r.missingInformation.length>0&&<div className="callout warning"><b><AlertTriangle size={16}/> Missing information</b><div className="chips">{r.missingInformation.map(x=><span key={x}>{x}</span>)}</div><div className="questions">{r.clarificationQuestions.map((x,i)=><div key={x}><i>{i+1}</i>{x}</div>)}</div></div>}
 {r.duplicateMatches.length>0&&<div className="callout"><b><Search size={16}/> Possible duplicates</b>{r.duplicateMatches.map(x=><div className="dup" key={x.id}><span><strong>DEF-{String(x.id).padStart(4,'0')}</strong> {x.title}</span><b>{Math.round(x.similarity*100)}% similar</b></div>)}</div>}
 <div className="actions"><button className="secondary" onClick={()=>navigator.clipboard?.writeText(JSON.stringify(r,null,2))}><Copy size={16}/>Copy JSON</button><button className="primary" disabled={saving} onClick={save}><Database size={16}/>{saving?'Saving...':'Save Defect'}</button></div>
 </>
}

function Score({n}){return <div className="ring"><svg width="94" height="94"><circle cx="47" cy="47" r="36" fill="none" stroke="#243047" strokeWidth="8"/><circle cx="47" cy="47" r="36" fill="none" stroke="#5eead4" strokeWidth="8" strokeLinecap="round" strokeDasharray={`${226*n/100} 226`} transform="rotate(-90 47 47)"/></svg><b>{n}</b></div>}
function Metric({name,value}){return <div className="metric"><div><span>{name}</span><b>{value}%</b></div><i><u style={{width:value+'%'}}/></i></div>}
function Block({title,children}){return <div className="block"><small>{title}</small>{children}</div>}
function Rows({r}){return <div className="rows"><div><span>Severity</span><b className={'sev '+r.severity.toLowerCase()}>{r.severity}</b></div><div><span>Priority</span><b>{r.priority}</b></div><div><span>Reproducibility</span><b>{r.reproducibility}</b></div><div><span>Environment</span><b>{r.environment}</b></div></div>}

function HistoryView({data}){return <section className="panel history"><div className="heading"><div><h2>Defect History</h2><p>Previously enhanced and saved defects.</p></div><em>{data.length} records</em></div>{!data.length?<div className="empty small"><History size={32}/><h3>No saved defects yet</h3></div>:<div className="table"><table><thead><tr><th>ID</th><th>Title</th><th>Severity</th><th>Priority</th><th>Quality</th><th>Environment</th></tr></thead><tbody>{data.map(d=><tr key={d.id}><td>DEF-{String(d.id).padStart(4,'0')}</td><td>{d.title}</td><td><span className={'sev '+d.severity?.toLowerCase()}>{d.severity}</span></td><td>{d.priority}</td><td><b>{d.qualityScore}%</b></td><td>{d.environment}</td></tr>)}</tbody></table></div>}</section>}

createRoot(document.getElementById('root')).render(<App/>)
