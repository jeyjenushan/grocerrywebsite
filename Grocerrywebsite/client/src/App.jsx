import { Route, Routes, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import { Toaster } from "react-hot-toast";
import Footer from "./components/Footer";
import Login from "./components/Login";
import Register from "./components/Register";
import ForgotPassword from "./components/ForgotPassword";
import { useAppContext } from "./context/AppContext";
import AllProducts from "./pages/AllProducts";
import ProductCategory from "./pages/ProductCategory";
import ProductDetails from "./pages/ProductDetails";
import Cart from "./pages/Cart";
import AddAddress from "./pages/AddAddress";
import MyOrders from "./pages/MyOrders";
import SellerLogin from "./components/Seller/SellerLogin";
import SellerLayout from "./pages/Seller/SellerLayout";
import AddProduct from "./pages/Seller/AddProduct";
import Orders from "./pages/Seller/Orders";
import ProductList from "./pages/Seller/ProductList";

function App() {
  const location = useLocation();
  const { showUserLogin, isSeller } = useAppContext();

  const hideNavbarFooterPaths = [
    "/seller",
    "/login",
    "/register",
    "/forgotPassword",
  ];
  const shouldHideNavbarFooter = hideNavbarFooterPaths.some((path) =>
    location.pathname.includes(path)
  );
  console.log(location.pathname);

  return (
    <div className="text-default min-h-screen text-gray-700 bg-white">
      {!shouldHideNavbarFooter && <Navbar />}
      <Toaster />
      <div
        className={`${
          shouldHideNavbarFooter ? "" : "px-6 md:px-16 lg:px-24 xl:px-32"
        }`}
      >
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgotPassword" element={<ForgotPassword />} />
          <Route path="/products" element={<AllProducts />} />
          <Route path="/products/:category" element={<ProductCategory />} />
          <Route path="/products/:category/:id" element={<ProductDetails />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/addAddress" element={<AddAddress />} />
          <Route path="my-orders" element={<MyOrders />} />

          <Route
            path="/seller"
            element={isSeller ? <SellerLayout /> : <SellerLogin />}
          >
            <Route index element={isSeller ? <AddProduct /> : null} />
            <Route path="product-list" element={<ProductList />} />
            <Route path="orders" element={<Orders />} />
          </Route>
        </Routes>
      </div>
      {!shouldHideNavbarFooter && <Footer />}
    </div>
  );
}

export default App;
