import './Button.css'

function ButtonPdf({ onClick, label }) {
    return (
        <div className='button-Pdf'>
            <button type="button" onClick={onClick}>{label}</button>
           
        </div>
    )
}
export default ButtonPdf;