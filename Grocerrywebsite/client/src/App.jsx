import { Navigate, Route, Routes, useLocation } from "react-router-dom";
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
import CustomToaster from "./components/CustomToaster";
import Verify from "./pages/Verify";

function App() {
  const location = useLocation();
  const { sellerToken, userToken } = useAppContext();

  const hideNavbarFooterPaths = [
    "/seller",
    "/login",
    "/register",
    "/forgotPassword",
  ];
  const shouldHideNavbarFooter = hideNavbarFooterPaths.some((path) =>
    location.pathname.includes(path)
  );

  return (
    <div className="text-default min-h-screen text-gray-700 bg-white">
      {!shouldHideNavbarFooter && <Navbar />}
      <CustomToaster />
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
          <Route path="/loader" element={<Verify />} />

          <Route
            path="/cart"
            element={
              userToken ? (
                <Cart />
              ) : (
                <Navigate to="/login" replace state={{ from: location }} />
              )
            }
          />
          <Route
            path="/addAddress"
            element={
              userToken ? (
                <AddAddress />
              ) : (
                <Navigate to="/login" replace state={{ from: location }} />
              )
            }
          />
          <Route
            path="/my-orders"
            element={
              userToken ? (
                <MyOrders />
              ) : (
                <Navigate to="/login" replace state={{ from: location }} />
              )
            }
          />

          <Route
            path="/seller"
            element={
              sellerToken ? (
                <SellerLayout />
              ) : (
                <Navigate to="/seller/login" replace />
              )
            }
          >
            <Route index element={<AddProduct />} />
            <Route path="product-list" element={<ProductList />} />
            <Route path="orders" element={<Orders />} />
          </Route>
          <Route path="/seller/login" element={<SellerLogin />} />
        </Routes>
      </div>
      {!shouldHideNavbarFooter && <Footer />}
    </div>
  );
}

export default App;
