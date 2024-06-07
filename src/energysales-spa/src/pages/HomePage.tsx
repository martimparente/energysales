import logoName from '../assets/logo+name.svg';

export function HomePage() {
    console.log("HomePage   started");


    return (
        <div>
            <h1>Home</h1>
            <img src={logoName}/>
        </div>
    )
}
